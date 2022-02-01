package org.egov.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestHeader;
import org.egov.config.FiscalEventConfiguration;
import org.egov.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProjectUtil {
    @Autowired
    FiscalEventConfiguration fiscalEventConfiguration;

    @Autowired
    ServiceRequestRepository serviceRequestRepository;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * @param requestHeader
     * @param projectIds
     * @param tenantId
     * @return
     */
    public Optional<JsonNode> validateProjectId(RequestHeader requestHeader, Set<String> projectIds, String tenantId) {
        if (requestHeader != null && projectIds != null && !projectIds.isEmpty() && StringUtils.isNotBlank(tenantId)) {

            Map<String, Object> projectValueMap = new HashMap<>();
            projectValueMap.put(MasterDataConstants.IDS, new ArrayList<>(projectIds));
            projectValueMap.put(MasterDataConstants.CRITERIA_TENANT_ID, tenantId);

            Map<String, Object> projectMap = new HashMap<>();
            projectMap.put(MasterDataConstants.REQUEST_HEADER, requestHeader);
            projectMap.put(MasterDataConstants.CRITERIA, projectValueMap);

            Object response = serviceRequestRepository.fetchResult(createSearchProjectUrl(), projectMap);
            JsonNode jsonNode = objectMapper.convertValue(response, JsonNode.class);
            return Optional.ofNullable(jsonNode);
        }
        return Optional.empty();
    }

    private String createSearchProjectUrl() {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(fiscalEventConfiguration.getIfixMasterProjectHost())
                .append(fiscalEventConfiguration.getIfixMasterProjectContextPath())
                .append(fiscalEventConfiguration.getIfixMasterProjectSearchPath());
        return uriBuilder.toString();
    }
}
