package org.egov.ifix.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestHeader;
import org.egov.ifix.exception.HttpCustomException;
import org.egov.ifix.models.ChartOfAccountResponse;
import org.egov.ifix.models.fiscalEvent.FiscalEventResponseDTO;
import org.egov.ifix.models.fiscalEvent.FiscalEventSearchRequestDTO;
import org.egov.ifix.models.fiscalEvent.FiscalSearchCriteriaDTO;
import org.egov.ifix.models.mgramseva.CreateChallanRequestDTO;
import org.egov.ifix.models.mgramseva.CreateChallanResponseDTO;
import org.egov.ifix.service.AuthTokenService;
import org.egov.ifix.utils.ApplicationConfiguration;
import org.egov.ifix.utils.RequestHeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.egov.ifix.utils.EventConstants.CREATE_CHALLAN;
import static org.egov.ifix.utils.EventConstants.TENANT_ID;

@Repository
@Slf4j
public class FiscalEventRepository {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    ApplicationConfiguration applicationConfiguration;

    @Autowired
    private RequestHeaderUtil requestHeaderUtil;


    public FiscalEventResponseDTO collectFiscalEvent(String receivers, String eventType) {
        FiscalEventResponseDTO fiscalEventResponseDTO = null;
        String url = applicationConfiguration.getIfixHost() + applicationConfiguration.getIfixEventSearchURL();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authTokenService.getKeyCloakAuthToken());

        FiscalEventSearchRequestDTO fiscalEventSearchRequestDTO = wrapFiscalEventSearchRequest(receivers, eventType);

        HttpEntity<FiscalEventSearchRequestDTO> entity = new HttpEntity<>(fiscalEventSearchRequestDTO, headers);

        try {
            ResponseEntity<FiscalEventResponseDTO> response = restTemplate.exchange(url, HttpMethod.POST, entity,
                    FiscalEventResponseDTO.class);

            fiscalEventResponseDTO = response.getBody();

            if (fiscalEventResponseDTO == null) {
                throw new HttpCustomException(CREATE_CHALLAN, "Unable get response from fiscal event",
                        HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            throw new HttpCustomException(CREATE_CHALLAN, "Exception while requesting from fiscal event search",
                    e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return fiscalEventResponseDTO;
    }

    /**
     * @return
     */
    private FiscalEventSearchRequestDTO wrapFiscalEventSearchRequest(String receivers, String eventType) {
        FiscalEventSearchRequestDTO fiscalEventSearchRequestDTO = new FiscalEventSearchRequestDTO();

        FiscalSearchCriteriaDTO fiscalSearchCriteriaDTO = new FiscalSearchCriteriaDTO();
        fiscalSearchCriteriaDTO.setTenantId(applicationConfiguration.getTenantId());
        fiscalSearchCriteriaDTO.setEventType(eventType);
        fiscalSearchCriteriaDTO.setFromEventTime(1653868800000L);       //TODO: it will be removed by actual time
        fiscalSearchCriteriaDTO.setToEventTime(1654165706000L);         //TODO: it will be removed by actual time

        fiscalEventSearchRequestDTO.setRequestHeader(new RequestHeader());
        fiscalEventSearchRequestDTO.setCriteria(fiscalSearchCriteriaDTO);

        return fiscalEventSearchRequestDTO;
    }
}
