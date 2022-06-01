package org.egov.mapper;

import client.stub.GetBillResult;
import client.stub.GetPaymentResult;
import lombok.extern.slf4j.Slf4j;
import org.egov.entity.BillJsonData;
import org.egov.entity.PaymentJsonData;
import org.egov.entity.PspclBillDetail;
import org.egov.entity.PspclPaymentDetail;
import org.egov.util.PspclIfixAdapterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.egov.util.PspclIfixAdapterConstant.BILL_ISSUE_DATE_FORMAT;
import static org.egov.util.PspclIfixAdapterConstant.TXN_DATE_FORMAT;

@Component
@Slf4j
public class PspclDataEntityMapper {

    @Autowired
    private PspclIfixAdapterUtil pspclIfixAdapterUtil;


    public PspclBillDetail mapPspclBillToEntity(GetBillResult currentMonthBill) {
        PspclBillDetail pspclBillDetail = new PspclBillDetail();
        BillJsonData billJsonData = new BillJsonData();

        billJsonData.setBILL_CYCLE(currentMonthBill.getBILL_CYCLE());
        billJsonData.setBILL_GROUP(currentMonthBill.getBILL_GROUP());
        billJsonData.setBILL_STATUS(currentMonthBill.getBILL_STATUS());

        billJsonData.setUSERID(currentMonthBill.getUSERID());
        billJsonData.setEMPID(currentMonthBill.getEMPID());
        billJsonData.setEMAIL_ID(currentMonthBill.getEMAIL_ID());

        billJsonData.setADD_SUR_AMT(currentMonthBill.getADD_SUR_AMT());
        billJsonData.setADD_SUR_RATE(currentMonthBill.getADD_SUR_RATE());
        billJsonData.setADD_SUR_UNITS(currentMonthBill.getADD_SUR_UNITS());

        billJsonData.setCGST(currentMonthBill.getCGST());
        billJsonData.setCIRCLE(currentMonthBill.getCIRCLE());

        billJsonData.setCONNECTED_LOAD(currentMonthBill.getCONNECTED_LOAD());
        billJsonData.setCONSUMER_ADDRESS(currentMonthBill.getCONSUMER_ADDRESS());
        billJsonData.setCONSUMER_GSTIN(currentMonthBill.getCONSUMER_GSTIN());
        billJsonData.setCONSUMER_NAME(currentMonthBill.getCONSUMER_NAME());
        billJsonData.setCONSUMPTION_1(currentMonthBill.getCONSUMPTION_1());
        billJsonData.setCONSUMPTION_2(currentMonthBill.getCONSUMPTION_2());
        billJsonData.setCONSUMPTION_3(currentMonthBill.getCONSUMPTION_3());
        billJsonData.setCONSUMPTION_4(currentMonthBill.getCONSUMPTION_4());
        billJsonData.setCONSUMPTION_5(currentMonthBill.getCONSUMPTION_5());
        billJsonData.setCONSUMPTION_6(currentMonthBill.getCONSUMPTION_6());

        billJsonData.setCURRENT_METER_CODE(currentMonthBill.getCURRENT_METER_CODE());
        billJsonData.setDATE_OF_CONNECTION(currentMonthBill.getDATE_OF_CONNECTION());

        billJsonData.setDIVISION(currentMonthBill.getDIVISION());

        billJsonData.setEC_000_100_KWH_AMOUNT(currentMonthBill.getEC_000_100_KWH_AMOUNT());
        billJsonData.setEC_000_100_KWH_RATE(currentMonthBill.getEC_000_100_KWH_RATE());
        billJsonData.setEC_000_100_KWH_UNITS(currentMonthBill.getEC_000_100_KWH_UNITS());
        billJsonData.setEC_101_300_KWH_AMOUNT(currentMonthBill.getEC_101_300_KWH_AMOUNT());
        billJsonData.setEC_101_300_KWH_RATE(currentMonthBill.getEC_101_300_KWH_RATE());
        billJsonData.setEC_101_300_KWH_UNITS(currentMonthBill.getEC_101_300_KWH_UNITS());
        billJsonData.setEC_301_500_KWH_AMOUNT(currentMonthBill.getEC_301_500_KWH_AMOUNT());
        billJsonData.setEC_301_500_KWH_RATE(currentMonthBill.getEC_301_500_KWH_RATE());
        billJsonData.setEC_301_500_KWH_UNITS(currentMonthBill.getEC_301_500_KWH_UNITS());
        billJsonData.setEC_501_ABOVE_KWH_AMOUNT(currentMonthBill.getEC_501_ABOVE_KWH_AMOUNT());
        billJsonData.setEC_501_ABOVE_KWH_RATE(currentMonthBill.getEC_501_ABOVE_KWH_RATE());
        billJsonData.setEC_501_ABOVE_KWH_UNITS(currentMonthBill.getEC_501_ABOVE_KWH_UNITS());
        billJsonData.setEC_KWH_TOTAL_AMOUNT(currentMonthBill.getEC_KWH_TOTAL_AMOUNT());

        billJsonData.setEE_QTY(currentMonthBill.getEE_QTY());
        billJsonData.setEE_RENT_CGST_AMT(currentMonthBill.getEE_RENT_CGST_AMT());
        billJsonData.setEE_NON_TAX_AMT(currentMonthBill.getEE_NON_TAX_AMT());
        billJsonData.setEE_RENT_SGST_AMT(currentMonthBill.getEE_RENT_SGST_AMT());
        billJsonData.setEE_RENT_TAX_AMT(currentMonthBill.getEE_RENT_TAX_AMT());
        billJsonData.setEE_RENT_TTL_AMT(currentMonthBill.getEE_RENT_TTL_AMT());

        billJsonData.setEXTRA1(currentMonthBill.getEXTRA1());
        billJsonData.setEXTRA2(currentMonthBill.getEXTRA2());
        billJsonData.setEXTRA3(currentMonthBill.getEXTRA3());
        billJsonData.setEXTRA4(currentMonthBill.getEXTRA4());
        billJsonData.setEXTRA5(currentMonthBill.getEXTRA5());
        billJsonData.setEXTRA6(currentMonthBill.getEXTRA6());
        billJsonData.setEXTRA7(currentMonthBill.getEXTRA7());
        billJsonData.setEXTRA8(currentMonthBill.getEXTRA8());
        billJsonData.setEXTRA9(currentMonthBill.getEXTRA9());
        billJsonData.setEXTRA10(currentMonthBill.getEXTRA10());

        billJsonData.setFC_BILL_PERIOD(currentMonthBill.getFC_BILL_PERIOD());
        billJsonData.setFC_LOAD(currentMonthBill.getFC_LOAD());
        billJsonData.setFC_RATE_KW_MON(currentMonthBill.getFC_RATE_KW_MON());
        billJsonData.setFC_TOTAL(currentMonthBill.getFC_TOTAL());
        billJsonData.setFEEDER_CODE(currentMonthBill.getFEEDER_CODE());

        billJsonData.setFUEL_COST_ADJ_AMOUNT(currentMonthBill.getFUEL_COST_ADJ_AMOUNT());
        billJsonData.setFUEL_COST_ADJ_RATE(currentMonthBill.getFUEL_COST_ADJ_RATE());
        billJsonData.setFUEL_COST_ADJ_UNITS(currentMonthBill.getFUEL_COST_ADJ_UNITS());

        billJsonData.setINTEREST_OF_SECURITY(currentMonthBill.getINTEREST_OF_SECURITY());
        billJsonData.setUNITS_CONCESSION(currentMonthBill.getUNITS_CONCESSION());
        billJsonData.setTOTAL_CONSUMPTION_OLD(currentMonthBill.getTOTAL_CONSUMPTION_OLD());
        billJsonData.setTOTAL_CONSUMPTION_NEW(currentMonthBill.getTOTAL_CONSUMPTION_NEW());
        billJsonData.setTNAME(currentMonthBill.getTNAME());
        billJsonData.setTAX_TOTAL(currentMonthBill.getTAX_TOTAL());
        billJsonData.setTAX_NET_MC_WITH_SIGN(currentMonthBill.getTAX_NET_MC_WITH_SIGN());
        billJsonData.setTAX_NET_IDF_WITH_SIGN(currentMonthBill.getTAX_NET_IDF_WITH_SIGN());
        billJsonData.setTAX_NET_ED_WITH_SIGN(currentMonthBill.getTAX_NET_ED_WITH_SIGN());
        billJsonData.setTAX_NET_COWCESS_WITH_SIGN(currentMonthBill.getTAX_NET_COWCESS_WITH_SIGN());

        billJsonData.setSYNCDT(currentMonthBill.getSYNCDT());
        billJsonData.setSUN_CHRG_TOTAL(currentMonthBill.getSUN_CHRG_TOTAL());
        billJsonData.setSUN_CHRG_TAXES(currentMonthBill.getSUN_CHRG_TAXES());
        billJsonData.setSUN_CHRG_NOTICE_NO(currentMonthBill.getSUN_CHRG_NOTICE_NO());
        billJsonData.setSUN_CHRG_NOTICE_DATE(currentMonthBill.getSUN_CHRG_NOTICE_DATE());
        billJsonData.setSUN_CHRG_FCA_PLUS_RENTALS(currentMonthBill.getSUN_CHRG_FCA_PLUS_RENTALS());
        billJsonData.setSUN_CHRG_EC(currentMonthBill.getSUN_CHRG_EC());
        billJsonData.setSUN_ALLW_TOTAL(currentMonthBill.getSUN_ALLW_TOTAL());
        billJsonData.setSUN_ALLW_TAXES(currentMonthBill.getSUN_ALLW_TAXES());
        billJsonData.setSUN_ALLW_NOTICE_NO(currentMonthBill.getSUN_ALLW_NOTICE_NO());
        billJsonData.setSUN_ALLW_NOTICE_DATE(currentMonthBill.getSUN_ALLW_NOTICE_DATE());
        billJsonData.setSUN_ALLW_FCA_PLUS_RENTALS(currentMonthBill.getSUN_ALLW_FCA_PLUS_RENTALS());
        billJsonData.setSUN_ALLW_EC(currentMonthBill.getSUN_ALLW_EC());
        billJsonData.setSUN_ALLW_CUR_PRE_ROUNDING(currentMonthBill.getSUN_ALLW_CUR_PRE_ROUNDING());
        billJsonData.setSUBSIDY_UNITS(currentMonthBill.getSUBSIDY_UNITS());
        billJsonData.setSUBSIDY_GOP_AMOUNT(currentMonthBill.getSUBSIDY_GOP_AMOUNT());
        billJsonData.setSUB_DIVISION_CODE(currentMonthBill.getSUB_DIVISION_CODE());
        billJsonData.setSUB_DIV_NAME(currentMonthBill.getSUB_DIV_NAME());
        billJsonData.setSC_WSD_AMT_WITHHELD(currentMonthBill.getSC_WSD_AMT_WITHHELD());
        billJsonData.setSGST(currentMonthBill.getSGST());
        billJsonData.setSECURITY_CONS_AMOUNT(currentMonthBill.getSECURITY_CONS_AMOUNT());
        billJsonData.setRENT_TOTAL(currentMonthBill.getRENT_TOTAL());
        billJsonData.setRENT_OTHER(currentMonthBill.getRENT_OTHER());
        billJsonData.setRENT_METER(currentMonthBill.getRENT_METER());
        billJsonData.setRENT_MCB(currentMonthBill.getRENT_MCB());
        billJsonData.setPRE_ARR_TOTAL(currentMonthBill.getPRE_ARR_TOTAL());
        billJsonData.setPRE_ARR_SURCHARGES(currentMonthBill.getPRE_ARR_SURCHARGES());
        billJsonData.setPRE_ARR_PENDING_AMOUNT(currentMonthBill.getPRE_ARR_PENDING_AMOUNT());
        billJsonData.setPRE_ARR_INTEREST(currentMonthBill.getPRE_ARR_INTEREST());
        billJsonData.setPRE_ADJ_TOTAL(currentMonthBill.getPRE_ADJ_TOTAL());
        billJsonData.setPRE_ADJ_TAXES(currentMonthBill.getPRE_ADJ_TAXES());
        billJsonData.setPRE_ADJ_FIXED_CHARGES(currentMonthBill.getPRE_ADJ_FIXED_CHARGES());
        billJsonData.setPRE_ADJ_FCA_PLUS_RENTALS(currentMonthBill.getPRE_ADJ_FCA_PLUS_RENTALS());
        billJsonData.setPRE_ADJ_ENERGY_CHARGES(currentMonthBill.getPRE_ADJ_ENERGY_CHARGES());

        billJsonData.setPAYMENT_HISTORY_1(currentMonthBill.getPAYMENT_HISTORY_1());
        billJsonData.setPAYMENT_HISTORY_2(currentMonthBill.getPAYMENT_HISTORY_2());
        billJsonData.setPAYMENT_HISTORY_3(currentMonthBill.getPAYMENT_HISTORY_3());
        billJsonData.setPAYMENT_HISTORY_4(currentMonthBill.getPAYMENT_HISTORY_4());
        billJsonData.setPAYMENT_HISTORY_5(currentMonthBill.getPAYMENT_HISTORY_5());
        billJsonData.setPAYMENT_HISTORY_6(currentMonthBill.getPAYMENT_HISTORY_6());

        billJsonData.setOLD_ACCOUNT_NO(currentMonthBill.getOLD_ACCOUNT_NO());
        billJsonData.setMTR_RENT_TTL_AMT(currentMonthBill.getMTR_RENT_TTL_AMT());
        billJsonData.setMTR_RENT_TAX_AMT(currentMonthBill.getMTR_RENT_TAX_AMT());
        billJsonData.setMTR_RENT_SGST_AMT(currentMonthBill.getMTR_RENT_SGST_AMT());
        billJsonData.setMTR_RENT_CGST_AMT(currentMonthBill.getMTR_RENT_CGST_AMT());
        billJsonData.setMTR_READING_PREV(currentMonthBill.getMTR_READING_PREV());
        billJsonData.setMTR_READING_CURR(currentMonthBill.getMTR_READING_CURR());
        billJsonData.setMTR_NO(currentMonthBill.getMTR_NO());
        billJsonData.setMTR_MAKE(currentMonthBill.getMTR_MAKE());
        billJsonData.setMTR_DIGIT(currentMonthBill.getMTR_DIGIT());
        billJsonData.setMTR_CAPACITY(currentMonthBill.getMTR_CAPACITY());
        billJsonData.setMRU(currentMonthBill.getMRU());
        billJsonData.setMOBILE_NO(currentMonthBill.getMOBILE_NO());
        billJsonData.setMETER_SECURITY_AMOUNT(currentMonthBill.getMETER_SECURITY_AMOUNT());
        billJsonData.setMETER_MULTIPLIER(currentMonthBill.getMETER_MULTIPLIER());
        billJsonData.setMESSAGE(currentMonthBill.getMESSAGE());
        billJsonData.setMCB_RENT_TTL_AMT(currentMonthBill.getMCB_RENT_TTL_AMT());
        billJsonData.setMCB_RENT_TAX_AMT(currentMonthBill.getMCB_RENT_TAX_AMT());
        billJsonData.setMCB_RENT_SGST_AMT(currentMonthBill.getMCB_RENT_SGST_AMT());
        billJsonData.setMCB_RENT_CGST_AMT(currentMonthBill.getMCB_RENT_CGST_AMT());
        billJsonData.setLPSC_2_PERCENT(currentMonthBill.getLPSC_2_PERCENT());
        billJsonData.setINV_REMARKS(currentMonthBill.getINV_REMARKS());


        pspclBillDetail.setBillJsonData(billJsonData);

        pspclBillDetail.setBILL_NO(currentMonthBill.getBILL_NO());

        pspclBillDetail.setBILL_ISSUE_DATE(pspclIfixAdapterUtil.format(BILL_ISSUE_DATE_FORMAT, currentMonthBill.getBILL_ISSUE_DATE()));
        pspclBillDetail.setACCOUNT_NO(currentMonthBill.getACCOUNT_NO());

        pspclBillDetail.setDATE_READING_CURR(pspclIfixAdapterUtil.format(BILL_ISSUE_DATE_FORMAT, currentMonthBill.getDATE_READING_CURR()));

        pspclBillDetail.setDATE_READING_PREV(pspclIfixAdapterUtil.format(BILL_ISSUE_DATE_FORMAT, currentMonthBill.getDATE_READING_PREV()));

        pspclBillDetail.setDUE_DATE_CASH_ONLINE(currentMonthBill.getDUE_DATE_CASH_ONLINE());
        pspclBillDetail.setDUE_DATE_CHEQUE_DD(currentMonthBill.getDUE_DATE_CHEQUE_DD());
        pspclBillDetail.setTARIFF_TYPE(currentMonthBill.getTARIFF_TYPE());
        pspclBillDetail.setPAYABLE_AMOUNT_UPTO_15_DAYS(currentMonthBill.getPAYABLE_AMOUNT_UPTO_15_DAYS());
        pspclBillDetail.setPAYABLE_AMOUNT_BY_DUE_DATE(currentMonthBill.getPAYABLE_AMOUNT_BY_DUE_DATE());
        pspclBillDetail.setORDERBYCOLUMN(currentMonthBill.getORDERBYCOLUMN());

        return pspclBillDetail;

    }

    public PspclPaymentDetail mapPspclPaymentToEntity(GetPaymentResult currentMonthPayment) {
        PspclPaymentDetail pspclPaymentDetail = new PspclPaymentDetail();
        PaymentJsonData paymentJsonData = new PaymentJsonData();


        paymentJsonData.setAGENCY_CODE(currentMonthPayment.getAGENCY_CODE());
        paymentJsonData.setBILYR(currentMonthPayment.getBILYR());
        paymentJsonData.setBRANCH(currentMonthPayment.getBRANCH());
        paymentJsonData.setCASHDESK(currentMonthPayment.getCASHDESK());
        paymentJsonData.setPAYMENT_MODE(currentMonthPayment.getPAYMENT_MODE());
        paymentJsonData.setCATEGORY(currentMonthPayment.getCATEGORY());
        paymentJsonData.setPAYMENT_MODE(currentMonthPayment.getPAYMENT_MODE());
        paymentJsonData.setCHEQUE_DATE(currentMonthPayment.getCHEQUE_DATE());
        paymentJsonData.setWATER_SUPPLY(currentMonthPayment.getWATER_SUPPLY());
        paymentJsonData.setVID(currentMonthPayment.getVID());
        paymentJsonData.setTOT_AMT(currentMonthPayment.getTOT_AMT());
        paymentJsonData.setTBL_NAME(currentMonthPayment.getTBL_NAME());
        paymentJsonData.setSYNCMSG(currentMonthPayment.getSYNCMSG());
        paymentJsonData.setSYNCED(currentMonthPayment.getSYNCED());
        paymentJsonData.setSYNCDT(currentMonthPayment.getSYNCDT());
        paymentJsonData.setSURCHARGE(currentMonthPayment.getSURCHARGE());
        paymentJsonData.setSUBDIVNAME(currentMonthPayment.getSUBDIVNAME());
        paymentJsonData.setSOP(currentMonthPayment.getSOP());
        paymentJsonData.setSEND_MOB_SMS(currentMonthPayment.getSEND_MOB_SMS());
        paymentJsonData.setSEND_MAIL_MSG(currentMonthPayment.getSEND_MAIL_MSG());
        paymentJsonData.setRECON_ID(currentMonthPayment.getRECON_ID());
        paymentJsonData.setRECEIPTNO(currentMonthPayment.getRECEIPTNO());
        paymentJsonData.setRECEIPTDT(currentMonthPayment.getRECEIPTDT());
        paymentJsonData.setPU_WEBHOOK_ID(currentMonthPayment.getPU_WEBHOOK_ID());
        paymentJsonData.setPG_STATUS(currentMonthPayment.getPG_STATUS());
        paymentJsonData.setPG_REF_ID(currentMonthPayment.getPG_REF_ID());
        paymentJsonData.setPG_ERR_MSG(currentMonthPayment.getPG_ERR_MSG());
        paymentJsonData.setPG_ERR_CODE(currentMonthPayment.getPG_ERR_CODE());
        paymentJsonData.setOCTRAI(currentMonthPayment.getOCTRAI());
        paymentJsonData.setMBNO(currentMonthPayment.getMBNO());
        paymentJsonData.setIF_SAP(currentMonthPayment.getIF_SAP());
        paymentJsonData.setEMAIL(currentMonthPayment.getEMAIL());
        paymentJsonData.setELID(currentMonthPayment.getELID());
        paymentJsonData.setED(currentMonthPayment.getED());
        paymentJsonData.setDIVNAME(currentMonthPayment.getDIVNAME());
        paymentJsonData.setCONSUMER_ID(currentMonthPayment.getCONSUMER_ID());
        paymentJsonData.setCODE_SDIV(currentMonthPayment.getCODE_SDIV());
        paymentJsonData.setCNAME(currentMonthPayment.getCNAME());
        paymentJsonData.setCIRNAME(currentMonthPayment.getCIRNAME());
        paymentJsonData.setCHEQUE_NO(currentMonthPayment.getCHEQUE_NO());
        paymentJsonData.setAMT_AFTER_DUEDATE(currentMonthPayment.getAMT_AFTER_DUEDATE());

        pspclPaymentDetail.setPaymentJsonData(paymentJsonData);

        pspclPaymentDetail.setACNO(currentMonthPayment.getACNO());
        pspclPaymentDetail.setAMT(currentMonthPayment.getAMT());
        pspclPaymentDetail.setBILCYC(currentMonthPayment.getBILCYC());
        pspclPaymentDetail.setBILGRP(currentMonthPayment.getBILGRP());
        pspclPaymentDetail.setBILISSDT(pspclIfixAdapterUtil.format(TXN_DATE_FORMAT, currentMonthPayment.getBILISSDT()));
        pspclPaymentDetail.setTXNID(currentMonthPayment.getTXNID());
        pspclPaymentDetail.setTXNDATE(pspclIfixAdapterUtil.format(TXN_DATE_FORMAT, currentMonthPayment.getTXNDATE()));
        pspclPaymentDetail.setSTATUS_P(currentMonthPayment.getSTATUS_P());
        pspclPaymentDetail.setDUEDTCHQ(pspclIfixAdapterUtil.format(TXN_DATE_FORMAT, currentMonthPayment.getDUEDTCHQ()));
        pspclPaymentDetail.setDUEDTCASH(pspclIfixAdapterUtil.format(TXN_DATE_FORMAT, currentMonthPayment.getDUEDTCASH()));
        pspclPaymentDetail.setBILNO(currentMonthPayment.getBILNO());

        return pspclPaymentDetail;
    }
}
