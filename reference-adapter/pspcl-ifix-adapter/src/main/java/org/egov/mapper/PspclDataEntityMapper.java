package org.egov.mapper;

import client.stub.BillResultData;
import client.stub.GetBillResult;
import client.stub.GetPaymentResult;
import client.stub.PaymentsResultData;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.AuditDetails;
import org.egov.entity.*;
import org.egov.util.PspclIfixAdapterUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;

import static org.egov.util.PspclIfixAdapterConstant.BILL_ISSUE_DATE_FORMAT;
import static org.egov.util.PspclIfixAdapterConstant.TXN_DATE_FORMAT;

@Component
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class PspclDataEntityMapper {

    @Autowired
    private PspclIfixAdapterUtil pspclIfixAdapterUtil;


    public PspclBillDetail mapPspclBillToEntity(BillResultData currentMonthBill) {
        PspclBillDetail pspclBillDetail = new PspclBillDetail();
        //BillJsonData billJsonData = new BillJsonData();
        BillDetailJson billDetailJson = new BillDetailJson();

        BeanUtils.copyProperties(currentMonthBill, billDetailJson);

        pspclBillDetail.setBillDetailJson(billDetailJson);

        pspclBillDetail.setBILL_NO(currentMonthBill.getBillNumber());

        pspclBillDetail.setBILL_ISSUE_DATE(pspclIfixAdapterUtil.format(BILL_ISSUE_DATE_FORMAT, currentMonthBill.getBillIssueDate()));
        pspclBillDetail.setACCOUNT_NO(currentMonthBill.getAccountNumber());

        pspclBillDetail.setDATE_READING_CURR(pspclIfixAdapterUtil.format(BILL_ISSUE_DATE_FORMAT, currentMonthBill.getBillIssueDate()));

        pspclBillDetail.setDATE_READING_PREV(pspclIfixAdapterUtil.format(BILL_ISSUE_DATE_FORMAT, currentMonthBill.getBillIssueDate()));

        pspclBillDetail.setDUE_DATE_CASH_ONLINE(currentMonthBill.getDueDateByCash());
        pspclBillDetail.setDUE_DATE_CHEQUE_DD(currentMonthBill.getDueDateByCheque());
        pspclBillDetail.setTARIFF_TYPE(currentMonthBill.getTariffType());
        pspclBillDetail.setPAYABLE_AMOUNT_UPTO_15_DAYS(currentMonthBill.getGrossAmount());
        pspclBillDetail.setPAYABLE_AMOUNT_BY_DUE_DATE(currentMonthBill.getDueAmount());
        //pspclBillDetail.setORDERBYCOLUMN(currentMonthBill.getORDERBYCOLUMN());

        AuditDetails auditDetails = new AuditDetails();
        auditDetails.setCreatedBy("pspcl-ifix-adapter");
        auditDetails.setLastModifiedBy("pspcl-ifix-adapter");
        auditDetails.setCreatedTime(new Date().getTime());
        auditDetails.setLastModifiedTime(new Date().getTime());

        pspclBillDetail.setAuditDetails(auditDetails);

        return pspclBillDetail;

    }

    public PspclPaymentDetail mapPspclPaymentToEntity(PaymentsResultData currentMonthPayment) {
        PspclPaymentDetail pspclPaymentDetail = new PspclPaymentDetail();
        PaymentDetailJson paymentDetailJson = new PaymentDetailJson();

        BeanUtils.copyProperties(currentMonthPayment, paymentDetailJson);

        pspclPaymentDetail.setPaymentDetailJson(paymentDetailJson);

        pspclPaymentDetail.setACNO(currentMonthPayment.getAccountumber());
        pspclPaymentDetail.setAMT(currentMonthPayment.getAmount());
        pspclPaymentDetail.setBILCYC(currentMonthPayment.getBillCycle());
        pspclPaymentDetail.setBILGRP(currentMonthPayment.getBillGroup());
        if(!ObjectUtils.isEmpty(currentMonthPayment.getBillIssueDate()))
         pspclPaymentDetail.setBILISSDT(pspclIfixAdapterUtil.formatTxnDate(currentMonthPayment.getBillIssueDate()));
        pspclPaymentDetail.setTXNID(currentMonthPayment.getTransactionId());
        if(!ObjectUtils.isEmpty(currentMonthPayment.getTransactionDate()))
            pspclPaymentDetail.setTXNDATE(pspclIfixAdapterUtil.formatTxnDate(currentMonthPayment.getTransactionDate()));
        pspclPaymentDetail.setSTATUS_P(currentMonthPayment.getStatus_p());
        if(!ObjectUtils.isEmpty(currentMonthPayment.getDueDateByCash())) {
            pspclPaymentDetail.setDUEDTCHQ(pspclIfixAdapterUtil.formatTxnDate(currentMonthPayment.getDueDateByCash()));
            pspclPaymentDetail.setDUEDTCASH(pspclIfixAdapterUtil.formatTxnDate(currentMonthPayment.getDueDateByCash()));
        }
        pspclPaymentDetail.setBILNO(currentMonthPayment.getBillNumber());

        AuditDetails auditDetails = new AuditDetails();
        auditDetails.setCreatedBy("pspcl-ifix-adapter");
        auditDetails.setLastModifiedBy("pspcl-ifix-adapter");
        auditDetails.setCreatedTime(new Date().getTime());
        auditDetails.setLastModifiedTime(new Date().getTime());

        pspclPaymentDetail.setAuditDetails(auditDetails);

        return pspclPaymentDetail;
    }
}
