package org.egov.ifix.master;


import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MasterDataEnricher {

    public JsonObject getMasterDataForProjectCode(String projectCode) {
        // 1 - Get Department Entity Details
        // 2 - Get Project Details (based on department entity uuid)
        // 3 - Get Expenditure Details (based on expenditure uuid extracted from project details)
        // 4 - Get Department Details (based on department uuid extracted from department entity details)
        // 5 - Add all these details to a json object
        return null;
    }

}
