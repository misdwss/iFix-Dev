package org.egov.model;

import lombok.Getter;
import lombok.Setter;
import org.egov.entity.PspclBillDetail;
import org.egov.entity.PspclPaymentDetail;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ReconcileVO {
    private PspclBillDetail currentPspclBillDetail;
    private List<PspclPaymentDetail> currentPspclPaymentDetails;
    
    private BigDecimal currentCalculatedBillAmt;

    private String departmentEntityName;
    private String departmentEntityCode;

    private boolean status;

    private boolean billReconcile;
    private boolean paymentReconcile;
}
