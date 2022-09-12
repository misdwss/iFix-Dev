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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class MigrationService {

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
                hierarchyLevelVsLabelMap.put(hierarchyObject.getLevel(), hierarchyObject.getLabel());
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

    public void migrateData(MigrationRequest request){
        hierarchyLevelVsLabelMap = loadDepartmentHierarchyLevel(request.getTenantId());
        Integer resumeFrom = repository.getOffsetToResumeFrom(request.getTenantId());
        PlainsearchCriteria criteria = PlainsearchCriteria.builder().tenantId(request.getTenantId()).build();
        FiscalEventPlainSearchRequest plainSearchRequest = FiscalEventPlainSearchRequest.builder().criteria(criteria).build();
        plainSearchRequest.setRequestHeader(request.getRequestHeader());
        Object countResponse = serviceRequestRepository.fetchResult(new StringBuilder(ifixFiscalEventServiceHost + "fiscal-event-service/events/v1/_count"), plainSearchRequest);
        FiscalEventCountResponse finalCountResponse = objectMapper.convertValue(countResponse, FiscalEventCountResponse.class);
        Long totalNumberOfRecords = finalCountResponse.getCount();
        log.info("Total number of records: " + totalNumberOfRecords);
        for(int i = resumeFrom; i <= totalNumberOfRecords/batchSize; i += 1){
            log.info("At page: " + i);

            plainSearchRequest.getCriteria().setOffset(i);
            plainSearchRequest.getCriteria().setLimit(batchSize);
            Object plainSearchResponse = serviceRequestRepository.fetchResult(new StringBuilder(ifixFiscalEventServiceHost + "fiscal-event-service/events/v1/_plainsearch"), plainSearchRequest);
            FiscalEventResponse response = objectMapper.convertValue(plainSearchResponse, FiscalEventResponse.class);
            enrichHierarchyMapForMigration(response.getFiscalEvent());
            producer.push("ifix-fiscal-events-migrate", ProducerPOJO.builder().requestInfo(new RequestInfo()).records(response.getFiscalEvent()).build());

            break;
        }
    }
}
