package org.egov.util;

public class MasterDataConstants {
    public static final String JSONPATH_ERROR = "JSONPATH_ERROR";
    public static final String FISCAL_EVENT = "FISCAL_EVENT";
    public static final String REFERENCE_ID = "REFERENCE_ID";
    public static final String EVENT_TYPE = "EVENT_TYPE";
    public static final String TO_EVENT_TIME = "TO_EVENT_TIME";
    public static final String FROM_EVENT_TIME = "FROM_EVENT_TIME";
    public static final String PARENT_EVENT_ID = "LINKED_EVENT_ID";

    private MasterDataConstants() {
    }

    public static final String IDS = "Ids";
    public static final String CRITERIA = "criteria";

    public static final String GOVERNMENT_ID = "GOVERNMENT_ID";
    public static final String TENANT_ID = "TENANT_ID";
    public static final String EAT_ID = "EAT_ID";
    public static final String LOCATION_ID = "LOCATION_ID";
    public static final String REQUEST_HEADER = "requestHeader";

    public static final String CRITERIA_TENANT_ID = "tenantId";

    public static final String TENANT_LIST = "$.government.*";
    public static final String COA_IDS_JSON_PATH = "$.chartOfAccounts.*.id";
    public static final String COA_CODES_JSON_PATH = "$.chartOfAccounts.*.coaCode";
    public static final String COA_JSON_PATH = "$.chartOfAccounts.*";

    public static final String SEARCH_CRITERIA = "SEARCH_CRITERIA";

}
