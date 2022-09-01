package org.egov.ifixespipeline.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.ifixespipeline.models.Ancestry;
import org.egov.ifixespipeline.models.FiscalEventRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class FiscalDataEnrichmentService {
    public void enrichFiscalData(FiscalEventRequest incomingData){
        HashMap<String, Object> attributes = (HashMap<String, Object>) incomingData.getFiscalEvent().getAttributes();

        HashMap<String, Object> departmentEntity = new HashMap<>();

        if(attributes.containsKey("departmentEntity"))
            departmentEntity = (HashMap<String, Object>) attributes.get("departmentEntity");

        List<HashMap<String, Object>> ancestryList = new ArrayList<>();

        if(departmentEntity.containsKey("ancestry"))
            ancestryList = (List<HashMap<String, Object>>) departmentEntity.get("ancestry");

        HashMap<String, String> hierarchyMap = new HashMap<>();

        ancestryList.forEach(ancestry -> {
            if(ancestry.containsKey("type") && ancestry.containsKey("code"))
                hierarchyMap.put((String)ancestry.get("type"), (String)ancestry.get("code"));
        });

        incomingData.getFiscalEvent().setHierarchyMap(hierarchyMap);

        log.info(incomingData.getFiscalEvent().toString());
    }
}
