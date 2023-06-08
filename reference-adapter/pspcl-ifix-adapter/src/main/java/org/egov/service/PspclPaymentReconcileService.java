package org.egov.service;

import client.stub.BillResultData;
import client.stub.GetPaymentResult;
import client.stub.PaymentsResultData;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.entity.PspclBillDetail;
import org.egov.entity.PspclPaymentDetail;
import org.egov.mapper.PspclDataEntityMapper;
import org.egov.model.ReconcileVO;
import org.egov.repository.PspclPaymentDetailRepository;
import org.egov.util.PspclIfixAdapterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.egov.util.PspclIfixAdapterConstant.BILL_ISSUE_DATE_FORMAT;

@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class PspclPaymentReconcileService {

    @Autowired
    private PspclDataEntityMapper pspclDataEntityMapper;

    @Autowired
    private PspclIfixAdapterUtil pspclIfixAdapterUtil;

    @Autowired
    private PspclPaymentDetailRepository paymentDetailRepository;

//TODO: what if we have 3 different new payment from last bill date
    public void reconcilePayment(List<PaymentsResultData> pspclPaymentResults, ReconcileVO reconcileVO) {
        pspclPaymentResults = getPaymentsDoneAfterGivenDate(pspclPaymentResults, reconcileVO.getCurrentPspclBillDetail().getBILL_ISSUE_DATE());
        if (pspclPaymentResults != null && !pspclPaymentResults.isEmpty()) {
            PaymentsResultData currentMonthPaymentResult = pspclPaymentResults.get(0);

            Optional<PspclPaymentDetail> optionalPspclPaymentDetail = paymentDetailRepository
                    .findByTXNIDAndAccountNumber(currentMonthPaymentResult.getTransactionId(), currentMonthPaymentResult.getAccountumber());
            if (optionalPspclPaymentDetail.isPresent()) {
                reconcileVO.setPaymentReconcile(true);
            } else {
                PspclPaymentDetail currentPspclPaymentDetail = pspclDataEntityMapper.mapPspclPaymentToEntity(currentMonthPaymentResult);
               // reconcileVO.setCurrentPspclPaymentDetail(currentPspclPaymentDetail);
            }
        }

    }

    public void reconcilePaymentV2(List<PaymentsResultData> pspclPaymentResults, ReconcileVO reconcileVO, String date) {
        PspclBillDetail pspclBillResultData = reconcileVO.getCurrentPspclBillDetail();
        Date latestBillIssueDate = pspclIfixAdapterUtil.format(BILL_ISSUE_DATE_FORMAT, date);
        pspclPaymentResults = getPaymentsDoneAfterGivenDate(pspclPaymentResults, latestBillIssueDate);
        List<PspclPaymentDetail> newPaymentsResults = new ArrayList<>();
        if (pspclPaymentResults != null && !pspclPaymentResults.isEmpty()) {
           //List<String> txnIds = pspclPaymentResults.stream().map(paymentsResultData -> {paymentsResultData.getTransactionDate() > pspclBillResultData})
            for (PaymentsResultData paymentsResultData : pspclPaymentResults) {
                Optional<PspclPaymentDetail> optionalPspclPaymentDetail = paymentDetailRepository
                        .findByTXNIDAndAccountNumber(paymentsResultData.getTransactionId(), paymentsResultData.getAccountumber());
                if (!optionalPspclPaymentDetail.isPresent()) {
                    PspclPaymentDetail currentPspclPaymentDetail = pspclDataEntityMapper.mapPspclPaymentToEntity(paymentsResultData);
                    if(ObjectUtils.isEmpty(currentPspclPaymentDetail.getBILISSDT()) || currentPspclPaymentDetail.getBILISSDT() !=null) {
                        currentPspclPaymentDetail.setBILISSDT(latestBillIssueDate);
                    }
                    newPaymentsResults.add(currentPspclPaymentDetail) ;
                }
            }
            reconcileVO.setPaymentReconcile(false);
            reconcileVO.setCurrentPspclPaymentDetails(newPaymentsResults);

        } else {
            reconcileVO.setPaymentReconcile(true);
            reconcileVO.setCurrentPspclPaymentDetails(null);
        }


    }

    public List<PaymentsResultData>  getPaymentsDoneAfterGivenDate (List<PaymentsResultData> pspclPaymentResults, Date date) {
        return pspclPaymentResults.stream().filter(paymentsResultData -> {
               return compareDate(paymentsResultData.getTransactionDate(), date);
         }).collect(Collectors.toList());

    }

    public List<PaymentsResultData>  getPaymentsDoneBeforeGivenDate (List<PaymentsResultData> pspclPaymentResults, Date date) {
        return pspclPaymentResults.stream().filter(paymentsResultData -> {
            return !compareDate(paymentsResultData.getTransactionDate(), date);
        }).collect(Collectors.toList());

    }



    public boolean compareDate(String txnDate , Date date ) {
        Date txnDate1 = pspclIfixAdapterUtil.formatTxnDate( txnDate);

        if(txnDate1.compareTo(date) > 0) {
            return true;
        }
        return false;

    }

}
