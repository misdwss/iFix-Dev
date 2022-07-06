package org.egov.ifix.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class ApplicationConfiguration {

    @Value("${kafka.topics.ifix.adaptor.mapper}")
    private String mapperTopicName;

    @Value("${kafka.topics.ifix.adaptor.error}")
    private String errorTopicName;

    @Value("${kafka.topics.ifix.adaptor.http.error}")
    private String httpErrorTopicName;

    @Value("${state.goverment.code}")
    private String tenantId;

    @Value("${keycloak.credentials.clientid}")
    private String clientId;

    @Value("${keycloak.credentials.clientsecret}")
    private String clientSecret;

    @Value("${keycloak.credentials.granttype}")
    private String grantType;

    @Value("${keycloak.host}")
    private String keyCloakHost;

    @Value("${keycloak.token.url}")
    private String keyCloakAuthApi;

    @Value("${ifix.host}")
    private String ifixHost;

    @Value("${ifix.event.url}")
    private String ifixEventApi;

    @Value("${ifix.event.search.url}")
    private String ifixEventSearchURL;

    @Value("${ifix.coa.search.url}")
    private String coaSearchApi;

    @Value("${ifix.project.search.url}")
    private String projectSearchApi;

    @Value("${ifix.department.entity.host}")
    private String departmentEntityHost;

    @Value("${ifix.department.entity.context.path}")
    private String departmentEntityContextPath;

    @Value("${ifix.department.entity.search.path}")
    private String departmentEntitySearchPath;

    @Value("${pspcl.ifix.event.receiver.name}")
    private String pspclIfixEventReceiverName;

    @Value("${ifix.fiscal.event.search.interval.time}")
    private String ifixFiscalEventSearchIntervalTime;

    @Value("${adapter.master.data.host}")
    private String adapterMasterDataHost;

    @Value("${adapter.master.data.context.path}")
    private String adapterMasterDataContextPath;

    @Value("${adapter.master.department.search.path}")
    private String adapterMasterDepartmentSearchPath;

    @Value("${adapter.master.expenditure.search.path}")
    private String adapterMasterExpenditureSearchPath;

    @Value("${adapter.master.project.search.path}")
    private String adapterMasterProjectSearchPath;

    @Value("${mgramseva.host}")
    private String mgramsevaHost;

    @Value("${mgramseva.oauth.access.token.url}")
    private String mgramsevaOauthAccessTokenURL;

    @Value("${mgramseva.create.challan.url}")
    private String mgramsevaCreateChallanURL;

    @Value("${mgramseva.update.challan.url}")
    private String mgramsevaUpdateChallanURL;

    @Value("${mgramseva.search.challan.url}")
    private String mgramsevaSearchChallanURL;

    @Value("${mgramseva.vendor.search.url}")
    private String mgramsevaVendorSearchURL;

    @Value("${mgramseva.pspcl.business.service}")
    private String mgramsevaPspclBusinessService;

    @Value("${mgramseva.pspcl.consumer.type}")
    private String mgramsevaPspclConsumerType;

    @Value("${mgramseva.pspcl.typeOfExpense}")
    private String mgramsevaPspclTypeOfExpense;

    @Value("${mgramseva.pspcl.vendor.id}")
    private String mgramsevaPspclVendorId;

    @Value("${mgramseva.pspcl.vendor.name}")
    private String mgramsevaPspclVendorName;

    @Value("${mgramseva.pspcl.tax.head.code}")
    private String mgramsevaPspclTaxHeadCode;

    @Value("${mdms.search.url}")
    private String mdmsSearchUrl;

    @Value("${mgramseva.billing.service.fetch.bill.url}")
    private String mgramsevaBillingServiceFetchBillURL;

    @Value("${mgramseva.collection.service.payments.create.url}")
    private String mgramsevaCollectionServicePaymentsCreateURL;

}
