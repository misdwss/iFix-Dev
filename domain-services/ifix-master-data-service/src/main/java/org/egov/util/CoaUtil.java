package org.egov.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestHeader;
import org.egov.config.MasterDataServiceConfiguration;
import org.egov.repository.ServiceRequestRepository;
import org.egov.web.models.ChartOfAccount;
import org.egov.web.models.Government;
import org.egov.web.models.GovernmentSearchCriteria;
import org.egov.web.models.GovernmentSearchRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

@Component
@Slf4j
public class CoaUtil {

    @Autowired
    private ServiceRequestRepository searchRequestRepository;

    @Autowired
    private MasterDataServiceConfiguration mdsConfiguration;

    /**
     * Search the Government service based on search criteria
     *
     * @param requestHeader
     * @param chartOfAccount
     */
    public List<Government> searchTenants(RequestHeader requestHeader, ChartOfAccount chartOfAccount) {
        GovernmentSearchRequest govtSearchRequest = createSearchTenantRequest(requestHeader, chartOfAccount);
        String url = createSearchTenantUrl();
        Object response = searchRequestRepository.fetchResult(url, govtSearchRequest);
        if (response != null) {
            LinkedHashMap linkedHashMap = (LinkedHashMap) response;
            return (List<Government>) linkedHashMap.get("government");
        }

        return Collections.emptyList();
    }

    /**
     * Create the government search Url
     *
     * @return
     */
    private String createSearchTenantUrl() {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(mdsConfiguration.getIfixMasterGovernmentHost())
                .append(mdsConfiguration.getIfixMasterGovernmentContextPath())
                .append(mdsConfiguration.getIfixMasterGovernmentSearchPath());
        return uriBuilder.toString();
    }

    private GovernmentSearchRequest createSearchTenantRequest(RequestHeader requestHeader, ChartOfAccount chartOfAccount) {
        GovernmentSearchRequest govtSearchRequest = new GovernmentSearchRequest();
        GovernmentSearchCriteria govtSearchCriteria = new GovernmentSearchCriteria();
        RequestHeader newRequestHeader = new RequestHeader();

        BeanUtils.copyProperties(requestHeader, newRequestHeader);

        List<String> ids = new ArrayList<>();
        if (StringUtils.isNotBlank(chartOfAccount.getTenantId())) {
            ids.add(chartOfAccount.getTenantId());
        }

        govtSearchCriteria.setIds(ids);
        govtSearchRequest.setCriteria(govtSearchCriteria);
        govtSearchRequest.setRequestHeader(newRequestHeader);

        return govtSearchRequest;
    }
}
