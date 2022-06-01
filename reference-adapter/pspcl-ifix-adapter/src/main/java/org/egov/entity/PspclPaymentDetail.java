package org.egov.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "pspcl_payment_detail")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class PspclPaymentDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    private String TXNID;
    private Date TXNDATE;
    private String ACNO;
    private String AMT;//payment
    private String BILCYC;
    private String BILGRP;
    private String BILNO;
    private Date BILISSDT;
    private Date DUEDTCASH;
    private Date DUEDTCHQ;
    private String STATUS_P;

    @Type(type = "jsonb")
    @Column(name = "json_data", columnDefinition = "jsonb")
    private PaymentJsonData paymentJsonData;
}
