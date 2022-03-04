package org.egov.ifix.master.fetcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.egov.ifix.exception.HttpCustomException;
import org.egov.ifix.utils.ApplicationConfiguration;
import org.egov.ifix.utils.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.egov.ifix.utils.EventConstants.PROJECT_CODE;

@Component
@Slf4j
public class ProjectFetcher {

    @Autowired
    ApplicationConfiguration applicationConfiguration;
    @Autowired
    ServiceRequestRepository serviceRequestRepository;
    @Autowired
    ObjectMapper objectMapper;

    public JsonObject getProjectDetailsOfDepartmentEntity(String departmentEntityUuid) {
        String tenantId = applicationConfiguration.getTenantId();
        JsonNode searchRequest = createProjectSearchRequest(tenantId, departmentEntityUuid);

        Object response = serviceRequestRepository.fetchResult(createProjectSearchUrl(), searchRequest);
        JsonNode responseJson = objectMapper.convertValue(response, JsonNode.class);

        JsonNode projectDetails = responseJson.get("project").get(0);

        ObjectNode project = objectMapper.createObjectNode();
        project.put("id", projectDetails.get("id").asText());
        project.put("code", projectDetails.get("code").asText());
        project.put("name", projectDetails.get("name").asText());
        project.put("expenditureId", projectDetails.get("expenditureId").asText());

        return JsonParser.parseString(project.toString()).getAsJsonObject();
    }

    private JsonNode createProjectSearchRequest(String tenantId, String departmentEntityUuid) {

        ObjectNode searchCriteria = objectMapper.createObjectNode();
        searchCriteria.put("departmentEntityId", departmentEntityUuid);
        searchCriteria.put("tenantId", tenantId);

        ObjectNode searchRequest = objectMapper.createObjectNode();
        searchRequest.set("requestHeader", objectMapper.createObjectNode());
        searchRequest.set("criteria", searchCriteria);

        return searchRequest;
    }


    private String createProjectSearchUrl() {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(applicationConfiguration.getAdapterMasterDataHost())
                .append(applicationConfiguration.getAdapterMasterDataContextPath())
                .append(applicationConfiguration.getAdapterMasterProjectSearchPath());
        return uriBuilder.toString();
    }

}
