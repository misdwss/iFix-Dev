package org.egov.ifix.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.egov.ifix.exception.GenericCustomException;
import org.egov.ifix.models.fiscalEvent.FiscalEvent;
import org.egov.ifix.models.fiscalEvent.FiscalEventAmountDTO;
import org.egov.ifix.models.fiscalEvent.FiscalEventResponseDTO;
import org.egov.ifix.models.mdms.Tenant;
import org.egov.ifix.models.mgramseva.*;
import org.egov.ifix.repository.FiscalEventRepository;
import org.egov.ifix.repository.MdmsRepository;
import org.egov.ifix.repository.MgramsevaChallanRepository;
import org.egov.ifix.repository.MgramsevaVendorRepository;
import org.egov.ifix.service.*;
import org.egov.ifix.utils.ApplicationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.egov.ifix.utils.EventConstants.*;

@Slf4j
@Service
public class PspclEventServiceImpl implements PspclEventService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationConfiguration applicationConfiguration;

    @Autowired
    private FiscalEventRepository fiscalEventRepository;

    @Autowired
    private MdmsRepository mdmsRepository;

    @Autowired
    private MgramsevaChallanRepository mgramsevaChallanRepository;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private EventPostingDetailService eventPostingDetailService;

    @Autowired
    private PspclEventPersistenceService pspclEventPersistenceService;

    @Autowired
    private MgramsevaVendorRepository mgramsevaVendorRepository;

    @Autowired
    private CollectionService collectionService;

    public void pushPspclEventToMgramsev_ol() {

    }

    /**
     * @param eventType
     */
    @Override
    public void pushPspclEventToMgramseva(String eventType) {
        FiscalEventResponseDTO fiscalEventResponseDTO = fiscalEventRepository
                .collectFiscalEvent(applicationConfiguration.getPspclIfixEventReceiverName(), eventType);

        if (fiscalEventResponseDTO == null) {
            throw new GenericCustomException(PSPCL, "Unable to fetch PSPCL event from fiscal event");
        }else {
            List<FiscalEvent> fiscalEventList = fiscalEventResponseDTO.getFiscalEvent();

            for (FiscalEvent fiscalEvent : fiscalEventList) {
                Optional<String> departmentEntityCodeOptional = getDepartmentEntityCode(fiscalEvent);

                if (!departmentEntityCodeOptional.isPresent()) {
                    throw new GenericCustomException(PSPCL, "Unable to get department Entity code");
                } else {
                    List<Tenant> tenantList = mdmsRepository.getMdmsTenantByCityCode(departmentEntityCodeOptional.get());

                    log.info("tenantList" + tenantList);
                    if (tenantList == null || tenantList.isEmpty()) {
                        throw new GenericCustomException(PSPCL, "Unable to get tenant details from MDMS");
                    } else {
                        for (Tenant tenant : tenantList) {
                            if (FISCAL_EVENT_RECEIPT.equalsIgnoreCase(eventType)) {
                                try {
                                    collectionService.makePspclPaymentByCollectionService(fiscalEvent, tenant.getCode());

                                    pspclEventPersistenceService.saveSuccessPspclEventDetail(tenant.getCode(),
                                            eventType);
                                } catch (Exception e) {
                                    log.error("Unable to push update challan into mgramseva", e);

                                    pspclEventPersistenceService.saveFailedPspclEventDetail(tenant.getCode(), eventType,
                                            new Gson().toJson(fiscalEvent), e.getMessage());
                                }
                            } else if (FISCAL_EVENT_DEMAND.equalsIgnoreCase(eventType)) {
                                try {
                                    List<CreateChallanRequestDTO> createChallanRequestList =
                                            wrapCreateChallanRequestDTO(fiscalEvent, tenant.getCode());

                                    for (CreateChallanRequestDTO createChallanRequest : createChallanRequestList) {
                                        CreateChallanResponseDTO createChallanResponse = mgramsevaChallanRepository
                                                .pushMgramsevaCreateChallanAPI(createChallanRequest);

                                        pspclEventPersistenceService.saveSuccessPspclEventDetail(tenant.getCode(),
                                                eventType);
                                    }
                                } catch (Exception e) {
                                    log.error("Unable to push create challan into mgramseva", e);

                                    pspclEventPersistenceService.saveFailedPspclEventDetail(tenant.getCode(), eventType,
                                            new Gson().toJson(fiscalEvent), e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private List<CreateChallanRequestDTO> wrapUpdateChallanRequestDTO(FiscalEvent fiscalEvent, String mgramsevaTenantId) {
        List<CreateChallanRequestDTO> createChallanRequestList = new ArrayList<>();

        if (fiscalEvent != null && fiscalEvent.getAmountDetails() != null && !fiscalEvent.getAmountDetails().isEmpty()
                && !StringUtils.isEmpty(mgramsevaTenantId)) {
            for (FiscalEventAmountDTO fiscalEventAmount : fiscalEvent.getAmountDetails()) {
                if (fiscalEventAmount.getAmount() == null) {
                    throw new GenericCustomException(PSPCL, "Unable to get amount for fiscal event: " + fiscalEvent.getId());
                } else {
                    Optional<MgramsevaChallanResponseDTO> oldestMgramsevaChallanOptional =
                            getOldestChallanByBillDate(mgramsevaTenantId);

                    if (!oldestMgramsevaChallanOptional.isPresent()) {
                        throw new GenericCustomException(PSPCL, "Unable to get challan from mgramseva");
                    } else {
                        MgramsevaChallanResponseDTO oldestMgramsevaChallan = oldestMgramsevaChallanOptional.get();

                        MgramsevaChallanRequestDTO challanRequestDTO = new MgramsevaChallanRequestDTO();
                        challanRequestDTO.setId(oldestMgramsevaChallan.getId());
                        challanRequestDTO.setChallanNo(oldestMgramsevaChallan.getChallanNo());
                        challanRequestDTO.setTenantId(oldestMgramsevaChallan.getTenantId());
                        challanRequestDTO.setBusinessService(oldestMgramsevaChallan.getBusinessService());
                        challanRequestDTO.setIsBillPaid(true);
                        challanRequestDTO.setApplicationStatus("ACTIVE");
//                        challanRequestDTO.setConsumerType(applicationConfiguration.getMgramsevaPspclConsumerType());
                        challanRequestDTO.setTypeOfExpense(oldestMgramsevaChallan.getTypeOfExpense());
                        challanRequestDTO.setVendor(mgramsevaVendorRepository.getVendorIdByTenantId(mgramsevaTenantId));                  //TODO: Need to check with Phani - fixed vendor id
                        challanRequestDTO.setVendorName(applicationConfiguration.getMgramsevaPspclVendorName());
                        challanRequestDTO.setBillDate(fiscalEvent.getEventTime());                                                          //TODO: Need to check with Pintu
                        challanRequestDTO.setTaxPeriodFrom(fiscalEventAmount.getFromBillingPeriod());
                        challanRequestDTO.setTaxPeriodTo(fiscalEventAmount.getToBillingPeriod());
                        challanRequestDTO.setPaidDate(fiscalEvent.getEventTime());

                        MgramsevaAmountDTO amountDTO = new MgramsevaAmountDTO();
                        amountDTO.setTaxHeadCode(applicationConfiguration.getMgramsevaPspclTaxHeadCode());
                        amountDTO.setAmount(fiscalEventAmount.getAmount().doubleValue());

                        challanRequestDTO.setAmount(Collections.singletonList(amountDTO));

                        createChallanRequestList.add(new CreateChallanRequestDTO(getMgramsevaRequestInfo(), challanRequestDTO));
                    }
                }
            }
        }else{
            throw new GenericCustomException(PSPCL, "Content missing from fiscal event " + fiscalEvent.getId());
        }
        return createChallanRequestList;
    }

    /**
     * @param fiscalEvent
     * @param mgramsevaTenantId
     * @return
     */
    private List<CreateChallanRequestDTO> wrapCreateChallanRequestDTO(FiscalEvent fiscalEvent, String mgramsevaTenantId) {
        List<CreateChallanRequestDTO> createChallanRequestList = new ArrayList<>();

        if (fiscalEvent != null && fiscalEvent.getAmountDetails() != null && !fiscalEvent.getAmountDetails().isEmpty()
                && !StringUtils.isEmpty(mgramsevaTenantId)) {
            for (FiscalEventAmountDTO fiscalEventAmount : fiscalEvent.getAmountDetails()) {
                if (fiscalEventAmount.getAmount() == null) {
                    throw new GenericCustomException(PSPCL, "Unable to get amount for fiscal event: " + fiscalEvent.getId());
                } else {
                    MgramsevaChallanRequestDTO challanRequestDTO = new MgramsevaChallanRequestDTO();
                    challanRequestDTO.setTenantId(mgramsevaTenantId);
                    challanRequestDTO.setBusinessService(applicationConfiguration.getMgramsevaPspclBusinessService());
                    challanRequestDTO.setConsumerType(applicationConfiguration.getMgramsevaPspclConsumerType());
                    challanRequestDTO.setTypeOfExpense(applicationConfiguration.getMgramsevaPspclTypeOfExpense());
                    challanRequestDTO.setVendor(mgramsevaVendorRepository.getVendorIdByTenantId(mgramsevaTenantId));                //TODO: Need to check with Phani - fixed vendor id
                    challanRequestDTO.setVendorName(applicationConfiguration.getMgramsevaPspclVendorName());                        //TODO: Need write new API for vendor search
                    challanRequestDTO.setBillDate(fiscalEvent.getEventTime());                                                      //TODO: Need to check with Pintu
                    challanRequestDTO.setIsBillPaid(false);
                    challanRequestDTO.setTaxPeriodFrom(fiscalEventAmount.getFromBillingPeriod());
                    challanRequestDTO.setTaxPeriodTo(fiscalEventAmount.getToBillingPeriod());

                    MgramsevaAmountDTO amountDTO = new MgramsevaAmountDTO();
                    amountDTO.setTaxHeadCode(applicationConfiguration.getMgramsevaPspclTaxHeadCode());
                    amountDTO.setAmount(fiscalEventAmount.getAmount().doubleValue());

                    challanRequestDTO.setAmount(Collections.singletonList(amountDTO));

                    createChallanRequestList.add(new CreateChallanRequestDTO(getMgramsevaRequestInfo(), challanRequestDTO));
                }
            }
        }else{
            throw new GenericCustomException(PSPCL, "Content missing from fiscal event " + fiscalEvent.getId());
        }
        return createChallanRequestList;
    }

    /**
     * @return
     */
    private MgramsevaRequestInfoDTO getMgramsevaRequestInfo() {
        MgramsevaRequestInfoDTO requestInfoDTO = new MgramsevaRequestInfoDTO();
        requestInfoDTO.setApiId(MGRAMSEVA_CHALLAN_API_ID);
        requestInfoDTO.setVer(MGRAMSEVA_CHALLAN_VERSION);
        requestInfoDTO.setAction(MGRAMSEVA_CHALLAN_CREATE_ACTION);
        requestInfoDTO.setDid(MGRAMSEVA_CHALLAN_DID);
        requestInfoDTO.setMsgId(MGRAMSEVA_CHALLAN_MSG_ID);
        requestInfoDTO.setAuthToken(authTokenService.getMgramsevaAccessToken());

        return requestInfoDTO;
    }

    /**
     * @param tenantId
     * @return
     */
    public Optional<MgramsevaChallanResponseDTO> getOldestChallanByBillDate(String tenantId) {
        MgramsevaChallanResponseDTO oldestChallanResponse = null;

        Optional<SearchChallanResponseDTO> searchChallanResponseDTOOptional = mgramsevaChallanRepository
                .searchChallan(tenantId, MGRAMSEVA_ELECTRICITY_BILL_EXPENSE_TYPE);

        if (!searchChallanResponseDTOOptional.isPresent()) {
            throw new GenericCustomException(PSPCL, "Unable to fetch challan from mgramseva");
        } else {
            SearchChallanResponseDTO searchChallanResponseDTO = searchChallanResponseDTOOptional.get();
            List<MgramsevaChallanResponseDTO> mgramsevaChallanResponseDTOList = searchChallanResponseDTO.getChallans();

            Comparator<MgramsevaChallanResponseDTO> challanComparator = Comparator.comparingLong(MgramsevaChallanResponseDTO::getBillDate);

            oldestChallanResponse = mgramsevaChallanResponseDTOList.stream()
                    .map(mgramsevaChallanResponseDTO -> mgramsevaChallanResponseDTO)
                    .max(challanComparator).get();

            log.info("oldestChallanResponse: " + oldestChallanResponse);
        }

        return Optional.ofNullable(oldestChallanResponse);
    }


    /**
     * @param fiscalEvent
     * @return
     */
    private Optional<String> getDepartmentEntityCode(FiscalEvent fiscalEvent) {
        String departmentEntityCode = null;

        if (fiscalEvent != null) {
            try {
                ObjectNode objectNodeAttribute = objectMapper
                        .readValue(objectMapper.writeValueAsString(fiscalEvent.getAttributes()), ObjectNode.class);

                if (!objectNodeAttribute.has("departmentEntity")) {
                    throw new GenericCustomException(PSPCL, "Department Entity is missing from attributes section");
                } else {
                    JsonNode departmentEntityJsonNode = objectNodeAttribute.get("departmentEntity");

                    departmentEntityCode = departmentEntityJsonNode.get("code").asText();
                }
            } catch (JsonMappingException e) {
                throw new GenericCustomException(PSPCL, "Json parsing error while to process department Entity code in fiscal event");
            } catch (JsonProcessingException e) {
                throw new GenericCustomException(PSPCL, "Json parsing error while to process department Entity code in fiscal event");
            } catch (Exception e) {
                throw new GenericCustomException(PSPCL, "Unable to process department Entity code in fiscal event");
            }
        }

        return Optional.ofNullable(departmentEntityCode);
    }
}
