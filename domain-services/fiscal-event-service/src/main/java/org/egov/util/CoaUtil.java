package org.egov.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestHeader;
import org.egov.config.FiscalEventConfiguration;
import org.egov.repository.ServiceRequestRepository;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class CoaUtil {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FiscalEventConfiguration configuration;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    /**
     * Get the COA Details from master data service
     *
     * @param requestHeader
     * @param coaIds
     * @param tenantId
     * @return
     */
    public List<String> getCOAIdsFromCOAService(RequestHeader requestHeader, Set<String> coaIds, String tenantId) {
        String url = createCoaSearchUrl();
        Map<String, Object> coaSearchRequest = createSearchCoaRequest(requestHeader, coaIds, tenantId);

        Object response = serviceRequestRepository.fetchResult(url, coaSearchRequest);
        List<String> responseCoaIds = null;
        try {
            responseCoaIds = JsonPath.read(response, MasterDataConstants.COA_IDS_JSON_PATH);
        } catch (Exception e) {
            throw new CustomException(MasterDataConstants.JSONPATH_ERROR, "Failed to parse coa response for coaIds");
        }
        return responseCoaIds;
    }

    private Map<String, Object> createSearchCoaRequest(RequestHeader requestHeader, Set<String> coaIds, String tenantId) {
        if (StringUtils.isNotBlank(tenantId)) {
            Map<String, Object> coaSearchRequest = new HashMap<>();
            Map<String, Object> criteria = new HashMap<>();
            criteria.put("Ids", coaIds);
            criteria.put("tenantId", tenantId);

            coaSearchRequest.put("requestHeader", requestHeader);
            coaSearchRequest.put("criteria", criteria);
            return coaSearchRequest;
        }

        return Collections.emptyMap();
    }

    private String createCoaSearchUrl() {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(configuration.getIfixMasterCoaHost())
                .append(configuration.getIfixMasterCoaContextPath()).append(configuration.getIfixMasterCoaSearchPath());
        return uriBuilder.toString();
    }
}
