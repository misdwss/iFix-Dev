package org.egov.ifixespipeline.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestHeader;
import org.egov.ifixespipeline.models.*;
import org.egov.ifixespipeline.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FiscalDataEnrichmentService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${ifix.department.entity.service.host}")
    private String ifixDeptEntityServiceHost;

    @Value("${ifix.department.entity.search.endpoint}")
    private String ifixDeptEntityServiceSearchEndpoint;

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

    public void enrichFiscalData(FiscalEventRequest incomingData){
        HashMap<String, Object> attributes = (HashMap<String, Object>) incomingData.getFiscalEvent().getAttributes();

        HashMap<String, Object> departmentEntity = new HashMap<>();

        if(attributes.containsKey("departmentEntity"))
            departmentEntity = (HashMap<String, Object>) attributes.get("departmentEntity");

        List<HashMap<String, Object>> ancestryList = new ArrayList<>();

        if(departmentEntity.containsKey("ancestry"))
            ancestryList = (List<HashMap<String, Object>>) departmentEntity.get("ancestry");

        HashMap<String, String> hierarchyMap = new HashMap<>();

        HashMap<Integer, String> hierarchyLevelVsLabelMap = loadDepartmentHierarchyLevel(incomingData.getFiscalEvent().getTenantId());

        ancestryList.forEach(ancestry -> {
            if(ancestry.containsKey("hierarchyLevel") && ancestry.containsKey("code"))
                hierarchyMap.put(hierarchyLevelVsLabelMap.get(ancestry.get("hierarchyLevel")), (String)ancestry.get("code"));
        });

        incomingData.getFiscalEvent().setHierarchyMap(hierarchyMap);

        log.info(incomingData.getFiscalEvent().toString());
    }

    private StringBuilder getIfixDepartmentEntityUri(){
        StringBuilder uri = new StringBuilder(ifixDeptEntityServiceHost);
        uri.append(ifixDeptEntityServiceSearchEndpoint);
        return uri;
    }

    public void enrichComputedFields(FiscalEventRequest incomingData) {
        Map<String, Object> computedFieldsMap = new HashMap<>();
        if(incomingData.getFiscalEvent().getEventType().equals(FiscalEvent.EventTypeEnum.Demand)){
            Long totalDemandAmount = 0l;
            for(int i = 0; i < incomingData.getFiscalEvent().getAmountDetails().size(); i++){
                totalDemandAmount = totalDemandAmount + Long.valueOf(incomingData.getFiscalEvent().getAmountDetails().get(i).getAmount().toString());
            }
            computedFieldsMap.put("netDemandAmount", totalDemandAmount);
        }else if(incomingData.getFiscalEvent().getEventType().equals(FiscalEvent.EventTypeEnum.Bill)){
            Long totalBillAmount = 0l;
            for(int i = 0; i < incomingData.getFiscalEvent().getAmountDetails().size(); i++){
                totalBillAmount = totalBillAmount - Long.valueOf(incomingData.getFiscalEvent().getAmountDetails().get(i).getAmount().toString());
            }
            computedFieldsMap.put("netBillAmount", totalBillAmount);
        }else if(incomingData.getFiscalEvent().getEventType().equals(FiscalEvent.EventTypeEnum.Receipt)){
            Long totalCollectionAmount = 0l;
            for(int i = 0; i < incomingData.getFiscalEvent().getAmountDetails().size(); i++){
                totalCollectionAmount = totalCollectionAmount - Long.valueOf(incomingData.getFiscalEvent().getAmountDetails().get(i).getAmount().toString());
            }
            computedFieldsMap.put("netCollectionAmount", totalCollectionAmount);
        }
        incomingData.getFiscalEvent().setComputedFields(computedFieldsMap);
    }
}
