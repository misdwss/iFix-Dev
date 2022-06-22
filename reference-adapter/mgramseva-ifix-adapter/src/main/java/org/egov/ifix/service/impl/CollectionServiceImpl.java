package org.egov.ifix.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.ifix.exception.HttpCustomException;
import org.egov.ifix.models.fiscalEvent.FiscalEvent;
import org.egov.ifix.models.fiscalEvent.FiscalEventAmountDTO;
import org.egov.ifix.models.mgramseva.*;
import org.egov.ifix.repository.BillingServiceRepository;
import org.egov.ifix.repository.CollectionServiceRepository;
import org.egov.ifix.service.AuthTokenService;
import org.egov.ifix.service.CollectionService;
import org.egov.ifix.utils.ApplicationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.egov.ifix.utils.EventConstants.*;

@Slf4j
@Service
public class CollectionServiceImpl implements CollectionService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationConfiguration applicationConfiguration;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private BillingServiceRepository billingServiceRepository;

    @Autowired
    private CollectionServiceRepository collectionServiceRepository;

    @Override
    public void makePspclPaymentByCollectionService(FiscalEvent fiscalEvent, String mgramsevaTenantId) {
        if (fiscalEvent != null && fiscalEvent.getAmountDetails() != null && !fiscalEvent.getAmountDetails().isEmpty()
                && !StringUtils.isEmpty(mgramsevaTenantId)) {

            for (FiscalEventAmountDTO fiscalEventAmount : fiscalEvent.getAmountDetails()) {

                Set<String> billIdSet = getBillId(mgramsevaTenantId);

                if (billIdSet != null && !billIdSet.isEmpty()) {
                    for (String billId : billIdSet) {
                        CreatePaymentRequestDTO createPaymentRequestDTO =
                                wrapCreatePaymentRequest(mgramsevaTenantId, billId, fiscalEventAmount.getAmount().doubleValue());

                        CreatePaymentResponseDTO createPaymentResponseDTO = collectionServiceRepository
                                .createPaymentInCollectionService(createPaymentRequestDTO);

                        if (createPaymentResponseDTO == null) {
                            throw new HttpCustomException(COLLECTION_SERVICE_PAYMENT_CREATE, "Unable to get data or invalid " +
                                    "data from create payment API from collection service", HttpStatus.BAD_REQUEST);
                        }
                    }
                } else {
                    throw new HttpCustomException(COLLECTION_SERVICE_PAYMENT_CREATE, "Unable to get bill id",
                            HttpStatus.BAD_REQUEST);
                }
            }
        }
    }



    private Set<String> getBillId(String tenantId) {
        Set<String> billIdSet = new HashSet<>();

        if (!StringUtils.isEmpty(tenantId)) {
            Optional<FetchBillResponseDTO> fetchBillResponseDTOOptional = billingServiceRepository
                    .fetchBillFromBillingService(tenantId, "EB-2022-23-1186");

            if (!fetchBillResponseDTOOptional.isPresent() || fetchBillResponseDTOOptional.get().getBill() == null
                    || fetchBillResponseDTOOptional.get().getBill().isEmpty()) {

                throw new HttpCustomException(COLLECTION_SERVICE_PAYMENT_CREATE, "Unable to get data or /invalid data " +
                        "from fetch bill of Billing Service", HttpStatus.BAD_REQUEST);
            } else {
                FetchBillResponseDTO fetchBillResponseDTO = fetchBillResponseDTOOptional.get();
                List<BillDTO> billDTOList = fetchBillResponseDTO.getBill();

                billIdSet =  billDTOList.stream()
                        .peek(billDTO -> {
                            if (billDTO.getBillDetails() == null || billDTO.getBillDetails().isEmpty()) {
                                log.error("Bill details is missing from Bill - while calling fetch bill");
                            }
                        })
                        .filter(billDTO -> billDTO.getBillDetails() != null)
                        .flatMap(billDTO -> billDTO.getBillDetails().stream())
                        .map(BillDetailDTO::getBillId)
                        .collect(Collectors.toSet());
            }
        }

        return billIdSet;
    }

    /**
     * @param tenantId
     * @param billId
     * @param amountPaid
     * @return
     */
    private CreatePaymentRequestDTO wrapCreatePaymentRequest(String tenantId, String billId, Double amountPaid) {
        MgramsevaRequestInfoDTO requestInfoDTO = new MgramsevaRequestInfoDTO();
        requestInfoDTO.setApiId(MGRAMSEVA_CHALLAN_API_ID);
        requestInfoDTO.setVer(MGRAMSEVA_CHALLAN_VERSION);
        requestInfoDTO.setAction(MGRAMSEVA_CHALLAN_CREATE_ACTION);
        requestInfoDTO.setDid(MGRAMSEVA_CHALLAN_DID);
        requestInfoDTO.setMsgId(MGRAMSEVA_CHALLAN_MSG_ID);
        requestInfoDTO.setAuthToken(authTokenService.getMgramsevaAccessToken());

        PaymentDetailDTO paymentDetailDTO = new PaymentDetailDTO();
        paymentDetailDTO.setBusinessService(EXPENSE_ELECTRICITY_BILL);
        paymentDetailDTO.setBillId(billId);
        paymentDetailDTO.setTotalAmountPaid(amountPaid);


        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setTenantId(tenantId);
        paymentDTO.setPaymentMode(CASH_PAYMENT_MODE);
        paymentDTO.setPaidBy(PSPCL);
        paymentDTO.setTotalAmountPaid(amountPaid);
        paymentDTO.setPaymentDetails(Collections.singletonList(paymentDetailDTO));

        return new CreatePaymentRequestDTO(requestInfoDTO, paymentDTO);
    }
}
