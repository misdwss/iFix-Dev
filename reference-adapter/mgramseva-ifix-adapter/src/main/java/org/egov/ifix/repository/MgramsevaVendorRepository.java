package org.egov.ifix.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.ifix.cache.AdapterCache;
import org.egov.ifix.exception.GenericCustomException;
import org.egov.ifix.exception.HttpCustomException;
import org.egov.ifix.models.mgramseva.*;
import org.egov.ifix.service.AuthTokenService;
import org.egov.ifix.utils.ApplicationConfiguration;
import org.egov.ifix.utils.RequestHeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static org.egov.ifix.utils.EventConstants.*;
import static org.egov.ifix.utils.EventConstants.CREATE_CHALLAN;

@Repository
@Slf4j
public class MgramsevaVendorRepository {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    ApplicationConfiguration applicationConfiguration;

    @Autowired
    private RequestHeaderUtil requestHeaderUtil;

    @Autowired
    private AdapterCache<String> vendorCache;


    public String getVendorIdByTenantId(String tenantId) {
        String cachedVendorId = vendorCache.getValue(tenantId);

        if (StringUtils.isEmpty(cachedVendorId)) {
            Optional<SearchVendorResponseDTO> searchVendorResponseDTOOptional = searchVendor(tenantId);

            if (searchVendorResponseDTOOptional.isPresent() && searchVendorResponseDTOOptional.get().getVendor() != null) {

                Optional<MgramsevaVendorDTO> vendorOptional = searchVendorResponseDTOOptional.get().getVendor()
                        .stream().findFirst();

                if (vendorOptional.isPresent() && !StringUtils.isEmpty(vendorOptional.get().getId())) {
                    vendorCache.putValue(tenantId, vendorOptional.get().getId());
                    cachedVendorId = vendorOptional.get().getId();
                } else {
                    log.error("Vendor does not exit in the vendor list");
                    throw new GenericCustomException(MGRAMSEVA_VENDOR_ID, "Vendor does not exit in the vendor list");
                }
            } else {
                log.error("Unable to get vendor list from vendor search");
                throw new GenericCustomException(MGRAMSEVA_VENDOR_ID, "Unable to get vendor list from vendor search");
            }
        }else {
            log.info(LOG_INFO_PREFIX + "got vendor from Cache" + cachedVendorId);
        }

        return cachedVendorId;
    }

    /**
     * @param tenantId
     * @return
     */
    public Optional<SearchVendorResponseDTO> searchVendor(String tenantId) {
        SearchVendorResponseDTO searchVendorResponse = null;

        String url = applicationConfiguration.getMgramsevaHost()
                + applicationConfiguration.getMgramsevaVendorSearchURL();

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam(MGRAMSEVA_TENANT_ID, tenantId)
                .queryParam("limit", -1)
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        SearchVendorRequestDTO searchVendorRequestDTO = wrapSearchVendorRequestDTO();

        HttpEntity<SearchVendorRequestDTO> entity = new HttpEntity<>(searchVendorRequestDTO, headers);

        try {

            ResponseEntity<SearchVendorResponseDTO> response = restTemplate.exchange(urlTemplate, HttpMethod.POST, entity,
                    SearchVendorResponseDTO.class);

            searchVendorResponse = response.getBody();

            if (searchVendorResponse == null) {
                throw new HttpCustomException(CREATE_CHALLAN, "Unable get response from search vendor",
                        HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            throw new HttpCustomException(CREATE_CHALLAN, "Exception while sending request to search vendor",
                    e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return Optional.ofNullable(searchVendorResponse);
    }

    /**
     * @return
     */
    private SearchVendorRequestDTO wrapSearchVendorRequestDTO() {
        MgramsevaRequestInfoDTO requestInfoDTO = new MgramsevaRequestInfoDTO();
        requestInfoDTO.setApiId(MGRAMSEVA_CHALLAN_API_ID);
        requestInfoDTO.setVer(MGRAMSEVA_CHALLAN_VERSION);
        requestInfoDTO.setAction(MGRAMSEVA_CHALLAN_CREATE_ACTION);
        requestInfoDTO.setDid(MGRAMSEVA_CHALLAN_DID);
        requestInfoDTO.setMsgId(MGRAMSEVA_CHALLAN_MSG_ID);
        requestInfoDTO.setAuthToken(authTokenService.getMgramsevaAccessToken());

        MgramsevaUserInfoDTO mgramsevaUserInfoDTO = new MgramsevaUserInfoDTO();

        return new SearchVendorRequestDTO(requestInfoDTO, mgramsevaUserInfoDTO);
    }
}
