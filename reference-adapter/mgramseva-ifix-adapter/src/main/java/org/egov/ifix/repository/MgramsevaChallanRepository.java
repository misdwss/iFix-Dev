package org.egov.ifix.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.ifix.exception.HttpCustomException;
import org.egov.ifix.models.mgramseva.*;
import org.egov.ifix.service.AuthTokenService;
import org.egov.ifix.utils.ApplicationConfiguration;
import org.egov.ifix.utils.RequestHeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.egov.ifix.utils.EventConstants.*;

@Repository
@Slf4j
public class MgramsevaChallanRepository {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    ApplicationConfiguration applicationConfiguration;

    @Autowired
    private RequestHeaderUtil requestHeaderUtil;


    /**
     * @param createChallanRequestDTO Parameter validation should be done before. This parameter is scenario based.
     * @return
     */
    public CreateChallanResponseDTO pushMgramsevaCreateChallanAPI(CreateChallanRequestDTO createChallanRequestDTO) {
        CreateChallanResponseDTO createChallanResponseDTO = null;
        String url = applicationConfiguration.getMgramsevaHost()
                + applicationConfiguration.getMgramsevaCreateChallanURL();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateChallanRequestDTO> entity = new HttpEntity<>(createChallanRequestDTO, headers);

        try {
            ResponseEntity<CreateChallanResponseDTO> response = restTemplate.exchange(url, HttpMethod.POST, entity,
                    CreateChallanResponseDTO.class);

            createChallanResponseDTO = response.getBody();

            if (createChallanResponseDTO == null) {
                throw new HttpCustomException(CREATE_CHALLAN, "Unable get response from create challan",
                        HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Exception while sending request to create challan", e);
            throw new HttpCustomException(CREATE_CHALLAN, e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return createChallanResponseDTO;
    }

    /**
     * @param createChallanRequestDTO Parameter validation should be done before. This parameter is scenario based.
     * @return
     */
    public CreateChallanResponseDTO pushMgramsevaUpdateChallanAPI(CreateChallanRequestDTO createChallanRequestDTO) {
        CreateChallanResponseDTO createChallanResponseDTO = null;
        String url = applicationConfiguration.getMgramsevaHost()
                + applicationConfiguration.getMgramsevaUpdateChallanURL();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateChallanRequestDTO> entity = new HttpEntity<>(createChallanRequestDTO, headers);

        try {
            ResponseEntity<CreateChallanResponseDTO> response = restTemplate.exchange(url, HttpMethod.POST, entity,
                    CreateChallanResponseDTO.class);

            createChallanResponseDTO = response.getBody();

            if (createChallanResponseDTO == null) {
                throw new HttpCustomException(CREATE_CHALLAN, "Unable get response from update challan",
                        HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            throw new HttpCustomException(CREATE_CHALLAN, "Exception while sending request to update challan",
                    e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return createChallanResponseDTO;
    }


    /**
     * @return
     */
    private CreateChallanRequestDTO wrapCreateChallanRequestDTO() {
        MgramsevaRequestInfoDTO requestInfoDTO = new MgramsevaRequestInfoDTO();
        requestInfoDTO.setApiId(MGRAMSEVA_CHALLAN_API_ID);
        requestInfoDTO.setVer(MGRAMSEVA_CHALLAN_VERSION);
        requestInfoDTO.setAction(MGRAMSEVA_CHALLAN_CREATE_ACTION);
        requestInfoDTO.setDid(MGRAMSEVA_CHALLAN_DID);
        requestInfoDTO.setMsgId(MGRAMSEVA_CHALLAN_MSG_ID);
        requestInfoDTO.setAuthToken(authTokenService.getMgramsevaAccessToken());

        MgramsevaChallanRequestDTO challanRequestDTO = new MgramsevaChallanRequestDTO();
        challanRequestDTO.setTenantId("pb.lodhipur");
        challanRequestDTO.setBusinessService("EXPENSE.SALARY");
        challanRequestDTO.setConsumerType("EXPENSE");
        challanRequestDTO.setTypeOfExpense("SALARY");                           //TODO: Need to check with Phani
        challanRequestDTO.setVendor("c08045bd-1bcc-4c5a-b7d1-5cc5b0fb714b");    //TODO: Need to check with Phani - fixed vendor id
        challanRequestDTO.setVendorName("IFIX");
        challanRequestDTO.setBillDate(1648751400000L);                          //TODO: Need to check with Phani
        challanRequestDTO.setIsBillPaid(false);
        challanRequestDTO.setTaxPeriodFrom(1648751400000L);
        challanRequestDTO.setTaxPeriodTo(1648751400000L);

        MgramsevaAmountDTO amountDTO = new MgramsevaAmountDTO();
        amountDTO.setTaxHeadCode("20201");
        amountDTO.setAmount(2000d);

        challanRequestDTO.setAmount(Collections.singletonList(amountDTO));

        return new CreateChallanRequestDTO(requestInfoDTO, challanRequestDTO);
    }

    /**
     * @param tenantId
     * @param expenseType
     * @return
     */
    public Optional<SearchChallanResponseDTO> searchChallan(String tenantId, String expenseType) {
        SearchChallanResponseDTO searchChallanResponseDTO = null;

        String url = applicationConfiguration.getMgramsevaHost()
                + applicationConfiguration.getMgramsevaSearchChallanURL();

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam(MGRAMSEVA_TENANT_ID, tenantId)
                .queryParam(MGRAMSEVA_EXPENSE_TYPE, expenseType)
                .queryParam(CHALLAN_IS_BILL_PAID, false)
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        SearchChallanRequestDTO searchChallanRequestDTO = wrapSearchChallanRequestDTO();

        HttpEntity<SearchChallanRequestDTO> entity = new HttpEntity<>(searchChallanRequestDTO, headers);

        try {
            ResponseEntity<SearchChallanResponseDTO> response = restTemplate.exchange(urlTemplate, HttpMethod.POST, entity,
                    SearchChallanResponseDTO.class);

            searchChallanResponseDTO = response.getBody();

            if (searchChallanResponseDTO == null) {
                throw new HttpCustomException(CREATE_CHALLAN, "Unable get response from search challan",
                        HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            throw new HttpCustomException(CREATE_CHALLAN, "Exception while sending request to create challan",
                    e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return Optional.ofNullable(searchChallanResponseDTO);
    }

    /**
     * @return
     */
    private SearchChallanRequestDTO wrapSearchChallanRequestDTO() {
        MgramsevaRequestInfoDTO requestInfoDTO = new MgramsevaRequestInfoDTO();
        requestInfoDTO.setApiId(MGRAMSEVA_CHALLAN_API_ID);
        requestInfoDTO.setVer(MGRAMSEVA_CHALLAN_VERSION);
        requestInfoDTO.setAction(MGRAMSEVA_CHALLAN_CREATE_ACTION);
        requestInfoDTO.setDid(MGRAMSEVA_CHALLAN_DID);
        requestInfoDTO.setMsgId(MGRAMSEVA_CHALLAN_MSG_ID);
        requestInfoDTO.setAuthToken(authTokenService.getMgramsevaAccessToken());


        MgramsevaUserInfoDTO mgramsevaUserInfoDTO = new MgramsevaUserInfoDTO();
        return new SearchChallanRequestDTO(requestInfoDTO, mgramsevaUserInfoDTO);
    }

}
