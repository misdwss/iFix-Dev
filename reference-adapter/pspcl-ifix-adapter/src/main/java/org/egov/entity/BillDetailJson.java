package org.egov.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillDetailJson {


    @JsonProperty("ACNO")
    private String accountNumber;

    @JsonProperty("CNAME")
    private String cosumerName;

    @JsonProperty("MBNO")
    private String mobileNumber;

    @JsonProperty("EMAIL")
    private String email;

    @JsonProperty("ADDRESS")
    private String address;

    @JsonProperty("SUBDIVISION")
    private String subDivision;

    @JsonProperty("DIVISION")
    private String division;

    @JsonProperty("CIRCLE")
    private String circle;

    @JsonProperty("BILLNO")
    private String  billNumber;

    @JsonProperty("BILLCYC")
    private String billCycle;

    @JsonProperty("BILISSDT")
    private String billIssueDate;

    @JsonProperty("BILLCAT")
    private String tariffType;


    @JsonProperty("DUEDTCASH")
    private String dueDateByCash;


    @JsonProperty("DUEDTCHQ")
    private String dueDateByCheque;


    @JsonProperty("DUEAMOUNT")
    private String dueAmount;

    @JsonProperty("SURCHARGE")
    private String surCharge;


    @JsonProperty("GROSSAMOUNT")
    private String grossAmount;

    @JsonProperty("SANCTIONEDLOAD")
    private String sanctionLoad;

    @JsonProperty("CONSUMEDUNITS")
    private String cosumedUnit;


}