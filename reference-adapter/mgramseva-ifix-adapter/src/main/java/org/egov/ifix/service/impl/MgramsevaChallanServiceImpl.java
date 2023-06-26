package org.egov.ifix.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.AuditDetails;
import org.egov.ifix.exception.GenericCustomException;
import org.egov.ifix.models.fiscalEvent.FiscalEvent;
import org.egov.ifix.models.fiscalEvent.FiscalEventAmountDTO;
import org.egov.ifix.models.mgramseva.*;
import org.egov.ifix.producer.Producer;
import org.egov.ifix.repository.MgramsevaChallanRepository;
import org.egov.ifix.repository.MgramsevaVendorRepository;
import org.egov.ifix.service.AuthTokenService;
import org.egov.ifix.service.MgramsevaChallanService;
import org.egov.ifix.service.MgramsevaVendorService;
import org.egov.ifix.service.PspclEventPersistenceService;
import org.egov.ifix.utils.ApplicationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static org.egov.ifix.utils.EventConstants.*;

@Slf4j
@Service
public class MgramsevaChallanServiceImpl implements MgramsevaChallanService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MgramsevaChallanRepository mgramsevaChallanRepository;

    @Autowired
    private ApplicationConfiguration applicationConfiguration;

    @Autowired
    private MgramsevaVendorRepository mgramsevaVendorRepository;

    @Autowired
    private MgramsevaVendorService mgramsevaVendorService;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private PspclEventPersistenceService pspclEventPersistenceService;

    public static final String CHALLAN_NUMBER = "challanNo";

    @Autowired
    Producer producer;

    /**
     * TODO: As of now, vendor id is mandatory to create challan.
     *      It gets vendor list by tenant id and pick first vendor from vendor list.
     *      In near future, challan may be created without vendor info.
     *
     * @param eventType
     * @param fiscalEvent
     * @param mgramsevaTenantId
     * @param pspclAccountNumber
     */
    @Override
    public void createChallan(String eventType, FiscalEvent fiscalEvent, String mgramsevaTenantId,
                              @NotNull String pspclAccountNumber) {
        if (fiscalEvent != null && fiscalEvent.getAmountDetails() != null && !fiscalEvent.getAmountDetails().isEmpty()
                && !StringUtils.isEmpty(mgramsevaTenantId)) {

            for (FiscalEventAmountDTO fiscalEventAmount : fiscalEvent.getAmountDetails()) {
                ChallanRequestDTO challanRequestDTO = new ChallanRequestDTO();
                challanRequestDTO.setTenantId(mgramsevaTenantId);
                challanRequestDTO.setBusinessService(applicationConfiguration.getMgramsevaPspclBusinessService());
                challanRequestDTO.setConsumerType(applicationConfiguration.getMgramsevaPspclConsumerType());
                challanRequestDTO.setTypeOfExpense(applicationConfiguration.getMgramsevaPspclTypeOfExpense());
                challanRequestDTO.setVendor(mgramsevaVendorService.getVendorIdByTenantId(mgramsevaTenantId,
                        applicationConfiguration.getMgramsevaPspclVendorName()));
                challanRequestDTO.setVendorName(applicationConfiguration.getMgramsevaPspclVendorName());
                challanRequestDTO.setBillDate(fiscalEvent.getEventTime());
                challanRequestDTO.setIsBillPaid(false);
                challanRequestDTO.setTaxPeriodFrom(fiscalEventAmount.getFromBillingPeriod());
                challanRequestDTO.setTaxPeriodTo(fiscalEventAmount.getToBillingPeriod());
                challanRequestDTO.setReferenceId(pspclAccountNumber);

                AmountDTO amountDTO = new AmountDTO();
                amountDTO.setTaxHeadCode(applicationConfiguration.getMgramsevaPspclTaxHeadCode());
                amountDTO.setAmount(fiscalEventAmount.getAmount().doubleValue());

                challanRequestDTO.setAmount(Collections.singletonList(amountDTO));

                mgramsevaChallanRepository.pushMgramsevaCreateChallanAPI(
                        new CreateChallanRequestDTO(getMgramsevaRequestInfo(), challanRequestDTO));

                pspclEventPersistenceService.saveSuccessPspclEventDetail(mgramsevaTenantId,
                        eventType, fiscalEvent.getId(), pspclAccountNumber,
                        fiscalEventAmount.getAmount().doubleValue());
            }
        } else {
            throw new GenericCustomException(PSPCL, "Content missing from fiscal event " + fiscalEvent);
        }
    }


    @Override
    public void updateChallan(String eventType, FiscalEvent fiscalEvent, String mgramsevaTenantId,
                              @NotNull String pspclAccountNumber, BillDetailDTO billDetailDTO) {
        if (fiscalEvent != null && fiscalEvent.getAmountDetails() != null && !fiscalEvent.getAmountDetails().isEmpty()
                && !StringUtils.isEmpty(mgramsevaTenantId)) {

            for (FiscalEventAmountDTO fiscalEventAmount : fiscalEvent.getAmountDetails()) {
                ChallanRequestDTO challanRequestDTO = new ChallanRequestDTO();
                challanRequestDTO.setTenantId(mgramsevaTenantId);
                challanRequestDTO.setBusinessService(applicationConfiguration.getMgramsevaPspclBusinessService());
                challanRequestDTO.setConsumerType(applicationConfiguration.getMgramsevaPspclConsumerType());
                challanRequestDTO.setTypeOfExpense(applicationConfiguration.getMgramsevaPspclTypeOfExpense());
                challanRequestDTO.setVendor(mgramsevaVendorService.getVendorIdByTenantId(mgramsevaTenantId,
                        applicationConfiguration.getMgramsevaPspclVendorName()));
                challanRequestDTO.setVendorName(applicationConfiguration.getMgramsevaPspclVendorName());
                challanRequestDTO.setBillDate(fiscalEventAmount.getFromBillingPeriod());
                challanRequestDTO.setIsBillPaid(true);
                challanRequestDTO.setApplicationStatus("PAID");
                if(!ObjectUtils.isEmpty(billDetailDTO.getAdditionalDetails())) {
                    HashMap<String, Object> additionalDetails = objectMapper.convertValue(billDetailDTO.getAdditionalDetails(),
                            HashMap.class);
                    if (additionalDetails.getOrDefault(CHALLAN_NUMBER, null) != null) {
                        challanRequestDTO.setChallanNo(additionalDetails.get(CHALLAN_NUMBER).toString());
                    }
                }

                challanRequestDTO.setPaidDate(fiscalEvent.getEventTime());
                challanRequestDTO.setTaxPeriodFrom(fiscalEventAmount.getFromBillingPeriod());
                challanRequestDTO.setTaxPeriodTo(fiscalEventAmount.getFromBillingPeriod());
                challanRequestDTO.setReferenceId(pspclAccountNumber);

                AmountDTO amountDTO = new AmountDTO();
                amountDTO.setTaxHeadCode(applicationConfiguration.getMgramsevaPspclTaxHeadCode());
                amountDTO.setAmount(fiscalEventAmount.getAmount().doubleValue());

                challanRequestDTO.setAmount(Collections.singletonList(amountDTO));
                AuditDetails auditDetails = new AuditDetails();
                auditDetails.setCreatedBy("pspcl-ifix-adapter");
                auditDetails.setLastModifiedBy("pspcl-ifix-adapter");
                auditDetails.setCreatedTime(new Date().getTime());
                auditDetails.setLastModifiedTime(new Date().getTime());
                challanRequestDTO.setAuditDetails(auditDetails);

                if(challanRequestDTO.getChallanNo()!=null) {
                    Optional<SearchChallanResponseDTO> searchChallanResponseDTOOptional = mgramsevaChallanRepository.searchChallan(mgramsevaTenantId,applicationConfiguration.getMgramsevaPspclTypeOfExpense(),challanRequestDTO.getChallanNo());
                    if(searchChallanResponseDTOOptional.isPresent()) {
                        SearchChallanResponseDTO searchChallanResponseDTO= searchChallanResponseDTOOptional.get();
                        List<ChallanResponseDTO> challanResponseDTOS = searchChallanResponseDTO.getChallans();
                       if(!challanResponseDTOS.isEmpty()) {
                           challanRequestDTO.setId(challanResponseDTOS.get(0).getId());
                           challanRequestDTO.setAccountId(challanResponseDTOS.get(0).getAccountId());
                       }
                    }
                   if(!ObjectUtils.isEmpty(challanRequestDTO.getId())) {
                       producer.push(applicationConfiguration.getUpdateChallanTopic(),challanRequestDTO);
                   }

                }


            }
        } else {
            throw new GenericCustomException(PSPCL, "Content missing from fiscal event " + fiscalEvent);
        }
    }

    /**
     * @return
     */
    private RequestInfoDTO getMgramsevaRequestInfo() {
        RequestInfoDTO requestInfoDTO = new RequestInfoDTO();
        requestInfoDTO.setApiId(MGRAMSEVA_CHALLAN_API_ID);
        requestInfoDTO.setVer(MGRAMSEVA_CHALLAN_VERSION);
        requestInfoDTO.setAction(MGRAMSEVA_CHALLAN_CREATE_ACTION);
        requestInfoDTO.setDid(MGRAMSEVA_CHALLAN_DID);
        requestInfoDTO.setMsgId(MGRAMSEVA_CHALLAN_MSG_ID);
        requestInfoDTO.setAuthToken(authTokenService.getMgramsevaAccessToken());

        return requestInfoDTO;
    }
}
