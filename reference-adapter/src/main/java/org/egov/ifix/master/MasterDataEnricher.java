package org.egov.ifix.master;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.egov.ifix.master.fetcher.DepartmentEntityFetcher;
import org.egov.ifix.master.fetcher.DepartmentFetcher;
import org.egov.ifix.master.fetcher.ExpenditureFetcher;
import org.egov.ifix.master.fetcher.ProjectFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MasterDataEnricher {

    @Autowired
    DepartmentEntityFetcher departmentEntityFetcher;
    @Autowired
    ProjectFetcher projectFetcher;
    @Autowired
    ExpenditureFetcher expenditureFetcher;
    @Autowired
    DepartmentFetcher departmentFetcher;

    public final JsonObject getMasterDataForProjectCode(String departmentEntityCode) {

        log.info("------------Master Data Enricher-----------------------");

        // 1 - Get Department Entity Details (based on departmentEntityCode)

        JsonObject departmentEntityDetails =
                departmentEntityFetcher.getDepartmentEntityDetailsFromCode(departmentEntityCode);

        String departmentUuid = departmentEntityDetails.get("departmentId").getAsString();
        departmentUuid = "af0739e0-dd6d-46dc-94fc-93e514e654fb";
        departmentEntityDetails.remove("departmentId");

        // 2 - Get Project Details (based on department entity uuid)
        String departmentEntityUuid = departmentEntityDetails.get("id").getAsString();
        departmentEntityUuid = "21bea728-623d-44d2-89ee-5fba7e0d4ae9";
        JsonObject projectDetails = projectFetcher.getProjectDetailsOfDepartmentEntity(departmentEntityUuid);

        String expenditureUuid = projectDetails.get("expenditureId").getAsString();
        expenditureUuid = "3d132c43-f366-423e-9080-28aead9adec2";
        projectDetails.remove("expenditureId");

        // 3 - Get Expenditure Details (based on expenditure uuid extracted from project details)
        JsonObject expenditureDetails = expenditureFetcher.getExpenditureDetails(expenditureUuid);

        // 4 - Get Department Details (based on department uuid extracted from department entity details)
        JsonObject departmentDetails = departmentFetcher.getDepartmentDetails(departmentUuid);

        // 5 - Add all these details to a json object
        JsonObject additionalAttributes = new JsonObject();
        additionalAttributes.add("departmentEntity", departmentEntityDetails);
        additionalAttributes.add("project", projectDetails);
        additionalAttributes.add("expenditure", expenditureDetails);
        additionalAttributes.add("department", departmentDetails);

        return additionalAttributes;
    }

}
