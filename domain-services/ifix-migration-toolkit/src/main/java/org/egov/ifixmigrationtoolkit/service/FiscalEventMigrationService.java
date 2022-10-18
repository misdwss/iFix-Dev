package org.egov.ifixmigrationtoolkit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestHeader;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ifixmigrationtoolkit.models.*;
import org.egov.ifixmigrationtoolkit.producer.Producer;
import org.egov.ifixmigrationtoolkit.repository.MigrationRepository;
import org.egov.ifixmigrationtoolkit.repository.ServiceRequestRepository;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Service
public class FiscalEventMigrationService {

    @Autowired
    private MigrationRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private Producer producer;

    @Value("${ifix.migration.batch.size}")
    private Integer batchSize;

    @Value("${ifix.migration.batch.push.topic}")
    private String batchMigrationPushTopic;

    @Value("${ifix.migration.progress.topic}")
    private String saveMigrationProgressTopic;

    @Value("${ifix.department.entity.service.host}")
    private String ifixDeptEntityServiceHost;

    @Value("${ifix.department.entity.search.endpoint}")
    private String ifixDeptEntityServiceSearchEndpoint;

    @Value("${ifix.fiscal.event.service.host}")
    private String ifixFiscalEventServiceHost;

    private HashMap<Integer, String> loadDepartmentHierarchyLevel(String tenantId){
        DepartmentHierarchyLevelSearchCriteria criteria = DepartmentHierarchyLevelSearchCriteria.builder().tenantId(tenantId).build();
        DepartmentHierarchyLevelSearchRequest request = DepartmentHierarchyLevelSearchRequest.builder().criteria(criteria).requestHeader(new RequestHeader()).build();
        Object result = serviceRequestRepository.fetchResult(getIfixDepartmentEntityUri(), request);
        DepartmentHierarchyLevelResponse response = objectMapper.convertValue(result, DepartmentHierarchyLevelResponse.class);
        HashMap<Integer, String> hierarchyLevelVsLabelMap = new HashMap<>();
        response.getDepartmentHierarchyLevel().forEach(hierarchyObject -> {
            if(!hierarchyLevelVsLabelMap.containsKey(hierarchyObject.getLevel()))
                hierarchyLevelVsLabelMap.put(hierarchyObject.getLevel(), hierarchyObject.getLabel().replaceAll(" ", "_"));
        });
        return hierarchyLevelVsLabelMap;
    }

    private HashMap<Integer, String> hierarchyLevelVsLabelMap;

    public void enrichHierarchyMapForMigration(List<FiscalEvent> listOfFiscalEvents){

        listOfFiscalEvents.forEach(incomingData->{
            HashMap<String, Object> attributes = (HashMap<String, Object>) incomingData.getAttributes();

            HashMap<String, Object> departmentEntity = new HashMap<>();

            if(attributes.containsKey("departmentEntity"))
                departmentEntity = (HashMap<String, Object>) attributes.get("departmentEntity");

            List<HashMap<String, Object>> ancestryList = new ArrayList<>();

            if(departmentEntity.containsKey("ancestry"))
                ancestryList = (List<HashMap<String, Object>>) departmentEntity.get("ancestry");

            HashMap<String, String> hierarchyMap = new HashMap<>();

            ancestryList.forEach(ancestry -> {
                if(ancestry.containsKey("hierarchyLevel") && ancestry.containsKey("code"))
                    hierarchyMap.put(hierarchyLevelVsLabelMap.get(ancestry.get("hierarchyLevel")), (String)ancestry.get("code"));
            });

            incomingData.setHierarchyMap(hierarchyMap);

        });
    }

    private StringBuilder getIfixDepartmentEntityUri(){
        StringBuilder uri = new StringBuilder(ifixDeptEntityServiceHost);
        uri.append(ifixDeptEntityServiceSearchEndpoint);
        return uri;
    }

    public Map<String, Object>  migrateData(MigrationRequest request){
        hierarchyLevelVsLabelMap = loadDepartmentHierarchyLevel(request.getTenantId());
        Integer resumeFrom = repository.getPageNumberToResumeFrom(request.getTenantId());
        Long numberOfRecordsMigrated = repository.getTotalNumberOfRecordsMigrated(request.getTenantId());
        PlainsearchCriteria criteria = PlainsearchCriteria.builder().tenantId(request.getTenantId()).build();
        FiscalEventPlainSearchRequest plainSearchRequest = FiscalEventPlainSearchRequest.builder().criteria(criteria).build();
        plainSearchRequest.setRequestHeader(request.getRequestHeader());
        Object countResponse = serviceRequestRepository.fetchResult(new StringBuilder(ifixFiscalEventServiceHost + "fiscal-event-service/events/v1/_count"), plainSearchRequest);
        FiscalEventCountResponse finalCountResponse = objectMapper.convertValue(countResponse, FiscalEventCountResponse.class);
        Long totalNumberOfRecords = finalCountResponse.getCount();
        Integer lastPageNumber = resumeFrom;
        Long totalNumberOfRecordsMigrated = numberOfRecordsMigrated;
        Map<String, Object> responseMap = new HashMap<>();
        int i = resumeFrom;
        while (Boolean.TRUE) {
            log.info("At page: " + i);
            plainSearchRequest.getCriteria().setOffset(i);
            plainSearchRequest.getCriteria().setLimit(batchSize);
            FiscalEventResponse response = null;
            try {
                Object plainSearchResponse = serviceRequestRepository.fetchResult(new StringBuilder(ifixFiscalEventServiceHost + "fiscal-event-service/events/v1/_plainsearch"), plainSearchRequest);
                response = objectMapper.convertValue(plainSearchResponse, FiscalEventResponse.class);

                if(CollectionUtils.isEmpty(response.getFiscalEvent()))
                    break;

                enrichHierarchyMapForMigration(response.getFiscalEvent());
            }catch (Exception e){
                log.error("IFIX_MIGRATION_ERR", "Some error occurred while migrating data");
                responseMap.put("IFIX_MIGRATION_ERR", "Error occurred while migrating data on page index: " + i);
                responseMap.put("IFIX_MIGRATION_INFO", "Number of records migrated: " + totalNumberOfRecordsMigrated);
                // Commit page number to resume migration from in case of any error while migrating
                commitMigrationProgress(request.getTenantId(), i, batchSize, totalNumberOfRecordsMigrated);
                return responseMap;
            }
            if(i == resumeFrom && totalNumberOfRecordsMigrated < totalNumberOfRecords){
                seekPointerToAvoidDuplication(response, totalNumberOfRecordsMigrated, batchSize);
            }
            totalNumberOfRecordsMigrated += response.getFiscalEvent().size();
            producer.push(batchMigrationPushTopic, ProducerPOJO.builder().requestInfo(new RequestInfo()).records(response.getFiscalEvent()).build());
            commitMigrationProgress(request.getTenantId(), i, batchSize, totalNumberOfRecordsMigrated);
            i += 1;
            lastPageNumber = i;
        }
        log.info("Number of records migrated in current session: " + totalNumberOfRecordsMigrated);
        responseMap.put("IFIX_MIGRATION_INFO", "Number of records migrated in current session: " + totalNumberOfRecordsMigrated);
        commitMigrationProgress(request.getTenantId(), lastPageNumber, batchSize, totalNumberOfRecordsMigrated);
        return responseMap;
    }

    private void seekPointerToAvoidDuplication(FiscalEventResponse response, Long totalNumberOfRecordsMigrated, Integer batchSize) {
        List<FiscalEvent> listOfFiscalEvents = new ArrayList<>();
        Long pointer = totalNumberOfRecordsMigrated % batchSize;
        String stringPointer = String.valueOf(pointer);
        for(int i = Integer.valueOf(stringPointer); i < response.getFiscalEvent().size(); i++){
            listOfFiscalEvents.add(response.getFiscalEvent().get(i));
        }
        response.setFiscalEvent(listOfFiscalEvents);
        log.info("No of records to be migrated post seeking pointer - " + response.getFiscalEvent().size());
    }

    private void commitMigrationProgress(String tenantId, Integer pageNumber, Integer batchSize, Long totalNumberOfRecordsMigrated){
        MigrationCount migrationCount = MigrationCount.builder()
                .id(UUID.randomUUID().toString())
                .createdTime(System.currentTimeMillis())
                .tenantId(tenantId)
                .pageNumber(pageNumber)
                .batchSize(batchSize)
                .totalNumberOfRecordsMigrated(totalNumberOfRecordsMigrated)
                .build();
        producer.push(saveMigrationProgressTopic, MigrationCountWrapper.builder().migrationCount(migrationCount).build());

    }
}
