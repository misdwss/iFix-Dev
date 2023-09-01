package org.egov.service;

import client.stub.BillResultData;
import client.stub.GetBillResult;
import client.stub.GetPaymentResult;
import client.stub.PaymentsResultData;
import org.egov.model.ReconcileVO;

import java.util.List;

public interface PspclBillAndPaymentReconcileService {

    ReconcileVO reconcile(List<BillResultData> pspclBillResults, List<PaymentsResultData> pspclPaymentResults) throws  Exception;

    void publishFiscalEvent(List<ReconcileVO> reconcileVOS);
}
