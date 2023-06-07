package org.egov.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDetailJson {


    @JsonProperty("TXNID")
    private String transactionId;

    @JsonProperty("TXNDATE")
    private String transactionDate;

    @JsonProperty("ACNO")
    private String accountumber;

    @JsonProperty("AMT")
    private String amount;

    @JsonProperty("BILCYC")
    private String billCycle;

    @JsonProperty("BILGRP")
    private String billGroup;

    @JsonProperty("BILNO")
    private String billNumber;

    @JsonProperty("BILISSDT")
    private String billIssueDate;

    @JsonProperty("DUEDTCASH")
    private String  dueDateByCash;

    @JsonProperty("CATEGORY")
    private String tariffType;

    @JsonProperty("STATUS_P")
    private String status_p;

    @JsonProperty("MBNO")
    private String mobileNumber;


    @JsonProperty("EMAIL")
    private String email;


    @JsonProperty("CNAME")
    private String consumerName;


    @JsonProperty("SUBDIVNAME")
    private String subDivision;

    @JsonProperty("DIVNAME")
    private String Division;


    @JsonProperty("CIRNAME")
    private String circle;

    @JsonProperty("RECEIPTNO")
    private String receiptNumber;

    @JsonProperty("RECEIPTDT")
    private String receiptDate;

    @JsonProperty("CODE_SDIV")
    private String codeSubDivision;

    @JsonProperty("SURCHARGE")
    private String surCharge;

    @JsonProperty("TOT_AMT")
    private String totalAmount;

    @JsonProperty("AMT_AFTER_DUEDATE")
    private String amountAfterDueDate;
}
