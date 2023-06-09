package org.egov.service;

import client.stub.BillResultData;
import client.stub.GetBillResult;
import client.stub.GetPaymentResult;
import client.stub.PaymentsResultData;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestHeader;
import org.egov.common.contract.request.UserInfo;
import org.egov.entity.PspclBillDetail;
import org.egov.entity.PspclPaymentDetail;
import org.egov.mapper.PspclDataEntityMapper;
import org.egov.model.FiscalEvent;
import org.egov.model.FiscalEventRequest;
import org.egov.model.ReconcileVO;
import org.egov.repository.PspclBillDetailRepository;
import org.egov.repository.PspclPaymentDetailRepository;
import org.egov.util.FiscalEventUtil;
import org.egov.util.PspclIfixAdapterUtil;
import org.egov.util.RequestHeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.egov.util.PspclIfixAdapterConstant.BILL_ISSUE_DATE_FORMAT;
import static org.egov.util.PspclIfixAdapterConstant.TXN_DATE_FORMAT;

@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class PspclBillAndPaymentReconcileServiceImpl implements PspclBillAndPaymentReconcileService {

    @Autowired
    private PspclIfixAdapterUtil pspclIfixAdapterUtil;

    @Autowired
    private PspclDataEntityMapper pspclDataEntityMapper;

    @Autowired
    private PspclBillDetailRepository billDetailRepository;

    @Autowired
    private PspclPaymentDetailRepository paymentDetailRepository;

    @Autowired
    private FiscalEventUtil fiscalEventUtil;

    @Autowired
    private RequestHeaderUtil requestHeaderUtil;

    @Autowired
    private PspclBillReconcileService billReconcileService;

    @Autowired
    private PspclPaymentReconcileService paymentReconcileService;


    @Override
    public ReconcileVO reconcile(List<BillResultData> pspclBillResults, List<PaymentsResultData> pspclPaymentResults) {
        log.info("Started - reconcile pspcl bills and payments...");
        ReconcileVO reconcileVO = new ReconcileVO();
        PspclBillDetail lastBillDetail = null;
        PspclPaymentDetail lastPaymentDetail = null;
        BigDecimal lastBillAmt = null;
        BigDecimal lastPaymentAmt = null;
        BigDecimal currentBillAmt = null;
        BigDecimal currentMonthBillAmt = null;

        //1. sort bills result based on 'BILL_ISSUE_DATE' and payments result based on 'TXNDATE'
        sortBillResultsWRTLatestBillIssueDate(pspclBillResults);
        log.info("pspclBillResults after sort by issue date" + pspclBillResults);
        sortPaymentResultsWRTTXNDate(pspclPaymentResults);
        log.info("pspclPaymentResults after sort by issue date" + pspclPaymentResults);
        //Check if it is the first bill inn the system . if yes only send annd save bill  fiscal event  no need to save and send payment fisacal  event
        if (billReconcileService.getBillByAccountNumber(pspclBillResults.get(0).getAccountNumber()) == 0) {
            billReconcileService.reconcileBill(pspclBillResults, reconcileVO);
            reconcileVO.setCurrentCalculatedBillAmt(new BigDecimal(reconcileVO.getCurrentPspclBillDetail().getPAYABLE_AMOUNT_BY_DUE_DATE()));
        } else {
            //Do reconcile bill to check if current bill already present in db
            billReconcileService.reconcileBill(pspclBillResults, reconcileVO);
            log.info("reconcileVO after Bill Reconcile" + reconcileVO.isBillReconcile());
            // if current bill is not present in db
            if (reconcileVO.getCurrentPspclBillDetail() != null && !reconcileVO.isBillReconcile()) {
                PspclBillDetail currentPspclBillDetail = reconcileVO.getCurrentPspclBillDetail();
                String accountNumber = currentPspclBillDetail != null ? currentPspclBillDetail.getACCOUNT_NO() : null;
                // we will send only the one result ill from db as it will be desc order and limit 1
                lastBillDetail = getLastBillDetailByAccountNumber(accountNumber);
                log.info("last bill date Date :" +lastBillDetail.getBILL_ISSUE_DATE().toString());
                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                String lastBillIssueDate = dateFormat.format(lastBillDetail.getBILL_ISSUE_DATE());
                log.info("last bill date Date in string formated:" + lastBillIssueDate);
                paymentReconcileService.reconcilePaymentV2(pspclPaymentResults, reconcileVO, lastBillIssueDate);
                Date lastBillIssueInDate = pspclIfixAdapterUtil.format(BILL_ISSUE_DATE_FORMAT, lastBillIssueDate);
                pspclPaymentResults = paymentReconcileService.getPaymentsDoneAfterGivenDate(pspclPaymentResults, lastBillIssueInDate);
                pspclPaymentResults =paymentReconcileService.getPaymentsDoneBeforeGivenDate(pspclPaymentResults,currentPspclBillDetail.getBILL_ISSUE_DATE());
                log.info("pspclPaymentResults size" + pspclPaymentResults.size());
                lastPaymentAmt = pspclPaymentResults.stream()
                        .map(pspclPaymentResultDetail -> new BigDecimal(pspclPaymentResultDetail.getAmount()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                log.info("last payment Amounnt till " + pspclPaymentResults.size());
                currentBillAmt = new BigDecimal(currentPspclBillDetail.getPAYABLE_AMOUNT_BY_DUE_DATE());
                if (lastBillDetail != null) {
                    lastBillAmt = new BigDecimal(lastBillDetail.getPAYABLE_AMOUNT_BY_DUE_DATE());
                } else {
                    lastBillAmt = new BigDecimal("0");
                }
                    //3. calculate the current bill and current payment
                currentMonthBillAmt = (currentBillAmt.subtract(lastBillAmt.subtract(lastPaymentAmt)));
                reconcileVO.setCurrentCalculatedBillAmt(currentMonthBillAmt);
                return reconcileVO;

            }
            // if current bill present in db, Check for any new payments done after the current bill date
            else {
                    paymentReconcileService.reconcilePaymentV2(pspclPaymentResults, reconcileVO,pspclBillResults.get(0).getBillIssueDate());

                }
            }


        return reconcileVO;
    }

    @Override
    public void publishFiscalEvent(List<ReconcileVO> reconcileVOS) {
        log.info("Preparing for fiscal event publish to iFix...");
        int chunkSize = 5;
        int chunkPointer = 0;
        if (reconcileVOS != null && !reconcileVOS.isEmpty()) {
            int totalPublishEventSize = reconcileVOS.size();
            int noOfChunk = (totalPublishEventSize / chunkSize);
            int leftChunkData = (totalPublishEventSize % chunkSize);
            //process - noOfChunk
            for (int chunkIndex = 0; chunkIndex < noOfChunk; chunkIndex++) {
                int dataIndex = 0;
                chunkPointer = createFiscalEventAndPublish(reconcileVOS, chunkSize, chunkPointer);
            }
            //process - leftChunkData
            if (leftChunkData != 0) {
                chunkPointer = createFiscalEventAndPublish(reconcileVOS, leftChunkData, chunkPointer);
            }

        }


    }

    private int createFiscalEventAndPublish(List<ReconcileVO> reconcileVOS, int chunkSize, int chunkPointer) {
        int dataIndex;
        List<FiscalEvent> fiscalEvents = new ArrayList<>();

        //create the fiscal event
        for (dataIndex = chunkPointer; dataIndex < (chunkPointer + chunkSize); dataIndex++) {
            ReconcileVO billAndPaymentEventDetail = reconcileVOS.get(dataIndex);
            FiscalEvent billFiscalEvent = fiscalEventUtil.getDemandFiscalEvent(billAndPaymentEventDetail);
            List<FiscalEvent> paymentFiscalEvents = fiscalEventUtil.getReceiptFiscalEvent(billAndPaymentEventDetail);

            if (billFiscalEvent != null) {
                fiscalEvents.add(billFiscalEvent);
            }
            if (paymentFiscalEvents != null) {
                for (FiscalEvent paymentFiscalEvent : paymentFiscalEvents) {
                    fiscalEvents.add(paymentFiscalEvent);
                }
            }
        }

        //publish the fiscal event
        FiscalEventRequest fiscalEventRequest = new FiscalEventRequest();
        RequestHeader requestHeader = requestHeaderUtil.getRequestHeader();
        requestHeader.setUserInfo(UserInfo.builder().uuid("pspcl-ifix-adapter").build());
        fiscalEventRequest.setFiscalEvent(fiscalEvents);
        fiscalEventRequest.setRequestHeader(requestHeader);


        fiscalEventUtil.publishFiscalEvent(fiscalEventRequest);

        chunkPointer = dataIndex;
        return chunkPointer;
    }

    private PspclPaymentDetail getLastPaymentDetails(Date lastBillDate, String accountNumber) {
        Date lastBillDateFormatted = pspclIfixAdapterUtil.format(BILL_ISSUE_DATE_FORMAT, lastBillDate);
        Date fromDate = pspclIfixAdapterUtil.format(TXN_DATE_FORMAT, lastBillDateFormatted);
        Date toDate = new Date();
        List<PspclPaymentDetail> dbPspclPaymentDetails = paymentDetailRepository.findByTXNDATEBetweenOrderByTXNDATEDescAndAccountNumber(fromDate, toDate, accountNumber);
        return (dbPspclPaymentDetails != null && !dbPspclPaymentDetails.isEmpty() ? dbPspclPaymentDetails.get(0) : null);
    }

    private PspclBillDetail getLastBillDetail(Date lastBillDate, String accountNumber) {
        Optional<PspclBillDetail> lastMonthBillOptional = billDetailRepository
                .findByBillIssueDateAndAccountNumber(lastBillDate, accountNumber);
        if (lastMonthBillOptional.isPresent())
            return lastMonthBillOptional.get();
        return null;
    }

    private PspclBillDetail getLastBillDetailByAccountNumber( String accountNumber) {
        Optional<PspclBillDetail> lastMonthBillOptional = billDetailRepository
                .findLastBillDetailsByAccountNumber(accountNumber);
        if (lastMonthBillOptional.isPresent())
            return lastMonthBillOptional.get();
        return null;
    }

    private void sortPaymentResultsWRTTXNDate(List<PaymentsResultData> pspclPaymentResults) {
        pspclPaymentResults.sort((paymentResult1, paymentResult2) -> {

            if (StringUtils.isNotBlank(paymentResult1.getTransactionDate())
                    && StringUtils.isNotBlank(paymentResult2.getTransactionDate())) {

                Date txnDate1 = pspclIfixAdapterUtil.formatTxnDate( paymentResult1.getTransactionDate());
                Date txnDate2 = pspclIfixAdapterUtil.formatTxnDate(paymentResult2.getTransactionDate());

                return (txnDate2.compareTo(txnDate1));
            }
            return 0;
        });
    }

    private void sortBillResultsWRTLatestBillIssueDate(List<BillResultData> pspclBillResults) {
        pspclBillResults.sort((billResult1, billResult2) -> {

            if (StringUtils.isNotBlank(billResult1.getBillIssueDate())
                    && StringUtils.isNotBlank(billResult2.getBillIssueDate())) {

                Date billIssueDate1 = pspclIfixAdapterUtil.format(BILL_ISSUE_DATE_FORMAT, billResult1.getBillIssueDate());
                Date billIssueDate2 = pspclIfixAdapterUtil.format(BILL_ISSUE_DATE_FORMAT, billResult2.getBillIssueDate());

                return (billIssueDate2.compareTo(billIssueDate1));
            }
            return 0;
        });
    }
}
