package org.egov.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "pspcl_bill_detail")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class PspclBillDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    private String ORDERBYCOLUMN;
    private String TARIFF_TYPE;
    private Date BILL_ISSUE_DATE;
    private Date DATE_READING_CURR;
    private Date DATE_READING_PREV;
    private String DUE_DATE_CASH_ONLINE;
    private String DUE_DATE_CHEQUE_DD;
    private String ACCOUNT_NO;
    private String PAYABLE_AMOUNT_BY_DUE_DATE;//actual bill amount
    private String PAYABLE_AMOUNT_UPTO_15_DAYS;
    private String BILL_NO;

    @Type(type = "jsonb")
    @Column(name = "json_data", columnDefinition = "jsonb")
    private BillJsonData billJsonData;

}
