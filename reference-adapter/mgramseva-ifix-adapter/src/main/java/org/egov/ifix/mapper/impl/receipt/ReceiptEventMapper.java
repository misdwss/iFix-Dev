package org.egov.ifix.mapper.impl.receipt;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.egov.ifix.mapper.EventMapper;
import org.egov.ifix.models.Amount;
import org.egov.ifix.models.EventTypeEnum;
import org.egov.ifix.models.FiscalEvent;
import org.egov.ifix.service.ChartOfAccountService;
import org.egov.ifix.service.ProjectService;
import org.egov.ifix.utils.ApplicationConfiguration;
import org.egov.ifix.utils.DataWrapper;
import org.egov.ifix.utils.EventConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.egov.ifix.utils.EventConstants.*;


@Component("RECEIPT")
@Slf4j
public class ReceiptEventMapper implements EventMapper {

    @Autowired
    private ApplicationConfiguration applicationConfiguration;

    @Autowired
    private ChartOfAccountService chartOfAccountService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DataWrapper dataWrapper;

    @Override
    public String getEventType() {
        return EventTypeEnum.RECEIPT.toString();
    }

    @Override
    public List<FiscalEvent> transformData(JsonObject data) {
        log.info(LOG_INFO_PREFIX + "Transforming RECEIPT Fiscal Event");

        List<FiscalEvent> fiscalEventList = new ArrayList<>();
        JsonArray entityJsonArray = data.getAsJsonObject(EVENT).getAsJsonArray(ENTITY);

        entityJsonArray.forEach(entityJsonElement -> {
            JsonObject paymentJsonObject = entityJsonElement.getAsJsonObject().getAsJsonObject(PAYMENT);

            FiscalEvent fiscalEvent = FiscalEvent.builder()
                    .tenantId(applicationConfiguration.getTenantId())
                    .eventType(getEventType())
                    .eventTime(paymentJsonObject.get(PAYMENT_RECEIPT_TRANSACTION_DATE).getAsLong())
                    .referenceId(paymentJsonObject.get(ID).getAsString())
                    .parentEventId(null).parentReferenceId(null)
                    .amountDetails(getAmountDetails(paymentJsonObject, data)).build();

            fiscalEventList.add(fiscalEvent);
        });

        log.info(EventConstants.LOG_INFO_PREFIX + "Transformed RECEIPT fiscal event: "
                + dataWrapper.getFiscalEventDetailsInformation(fiscalEventList));
        return fiscalEventList;
    }

    /**
     * @param data
     * @return
     */
    @Override
    public List<String> getReferenceIdList(JsonObject data) {
        List<String> referenceIdList = new ArrayList<>();
        JsonArray entityJsonArray = data.getAsJsonObject(EVENT).getAsJsonArray(ENTITY);

        entityJsonArray.forEach(entityJsonElement -> {
            JsonObject paymentJsonObject = entityJsonElement.getAsJsonObject().getAsJsonObject(PAYMENT);
            referenceIdList.add(paymentJsonObject.get(ID).getAsString());
        });

        return referenceIdList;
    }

    /**
     * @param payment
     * @param data
     * @return
     */
    private List<Amount> getAmountDetails(JsonObject payment, JsonObject data) {
        List<Amount> amountList = new ArrayList<>();

        if (payment != null && data != null) {
            JsonObject paymentDetailsJB = payment.getAsJsonArray(PAYMENT_DETAILS).get(0).getAsJsonObject();
            JsonArray billDetailsJA = paymentDetailsJB.getAsJsonObject(BILL).getAsJsonArray(BILL_DETAILS);

            billDetailsJA.forEach(billDetailsJE -> {
                Long taxPeriodFrom = billDetailsJE.getAsJsonObject().get(PAYMENT_RECEIPT_FROM_BILLING_PERIOD).getAsLong();
                Long taxPeriodTo = billDetailsJE.getAsJsonObject().get(PAYMENT_RECEIPT_TO_BILLING_PERIOD).getAsLong();
                JsonArray billAccountDetailsJA = billDetailsJE.getAsJsonObject().getAsJsonArray(BILL_ACCOUNT_DETAILS);

                billAccountDetailsJA.forEach(billAccountDetailsJE -> {
                    JsonObject billAccountDetailsJB = billAccountDetailsJE.getAsJsonObject();
                    String coaCode = billAccountDetailsJB.get(PAYMENT_RECEIPT_CLIENT_COA_CODE).getAsString();

                    if (new BigDecimal(0).compareTo(
                            billAccountDetailsJB.get(PAYMENT_RECEIPT_CLIENT_COA_AMOUNT).getAsBigDecimal()) == 0) {

                        Amount amount = Amount.builder()
                                .amount(billAccountDetailsJB.get(PAYMENT_RECEIPT_CLIENT_COA_AMOUNT).getAsBigDecimal())
                                .coaCode(chartOfAccountService.getResolvedChartOfAccountCode(coaCode))
                                .fromBillingPeriod(taxPeriodFrom)
                                .toBillingPeriod(taxPeriodTo).build();

                        amountList.add(amount);
                    }
                });
            });
        }

        return amountList;
    }
}
