package org.egov.ifix.aggregate.util;

public class FiscalEventAggregateConstants {

    private FiscalEventAggregateConstants(){}

    public static final String DEFAULT_FISCAL_PERIOD = "2021-22";
    public static final String START_YEAR = "START_YEAR";
    public static final String END_YEAR = "END_YEAR";
    public static final int DEFAULT_HIERARCHY_LEVEL = 6;
    public static final String VER = "1.0.0";

    public static final String EVENT_TYPE_DEMAND= "DEMAND";
    public static final String EVENT_TYPE_RECEIPT= "RECEIPT";
    public static final String EVENT_TYPE_PAYMENT = "PAYMENT";
    public static final String EVENT_TYPE_BILL= "BILL";

    public static final String EVENT_TYPE_PENDING_COLLECTION = "PENDING_COLLECTION";
    public static final String EVENT_TYPE_PENDING_PAYMENT = "PENDING_PAYMENT";
}
