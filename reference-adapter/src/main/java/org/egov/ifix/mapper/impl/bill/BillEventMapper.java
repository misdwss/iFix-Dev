package org.egov.ifix.mapper.impl.bill;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.egov.ifix.utils.EventConstants.*;

@Component("BILL")
@Slf4j
public class BillEventMapper implements EventMapper {

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
        return EventTypeEnum.BILL.toString();
    }

    /**
     * @param data
     * @return
     */
    @Override
    public List<FiscalEvent> transformData(JsonObject data) {
        log.info(LOG_INFO_PREFIX + "Transforming BILL Fiscal Event");

        List<FiscalEvent> fiscalEventList = new ArrayList<>();
        String clientProjectCode = data.getAsJsonObject(EVENT).get(PROJECT_ID)
                .getAsString();
        String iFixProjectId = projectService.getResolvedProjectId(clientProjectCode, data);
        JsonArray entityJsonArray = data.getAsJsonObject(EVENT).getAsJsonArray(ENTITY);

        entityJsonArray.forEach(jsonElement -> {
            JsonArray demandJsonArray = jsonElement.getAsJsonObject().getAsJsonArray(DEMANDS);

            demandJsonArray.forEach(demandJsonElement -> {
                JsonObject demandJsonObject = demandJsonElement.getAsJsonObject();

                FiscalEvent fiscalEvent = FiscalEvent.builder().tenantId(applicationConfiguration.getTenantId())
                        .eventType(getEventType())
                        .eventTime(Instant.now().toEpochMilli())
                        .referenceId(demandJsonObject.get(REFERENCE_ID).getAsString())
                        .parentEventId(null)
                        .parentReferenceId(null)
                        .amountDetails(getAmounts(demandJsonObject, data))
                        .projectId(iFixProjectId).build();

                fiscalEventList.add(fiscalEvent);
            });
        });
        log.info(LOG_INFO_PREFIX + "Transformed BILL fiscal event: "
                + dataWrapper.getFiscalEventDetailsInformation(fiscalEventList));
        return fiscalEventList;
    }

    /**
     * @param demand
     * @param data
     * @return
     */
    private List<Amount> getAmounts(JsonObject demand, JsonObject data) {
        List<Amount> amountList = new ArrayList<>();

        Long taxPeriodFrom = demand.get(BILL_DEMAND_FROM_BILLING_PERIOD).getAsLong();
        Long taxPeriodTo = demand.get(BILL_DEMAND_TO_BILLING_PERIOD).getAsLong();
        JsonArray demandDetailsArray = demand.getAsJsonArray(DEMAND_DETAILS);

        demandDetailsArray.forEach(jsonElement -> {
            JsonObject demandDetailJsonObject = jsonElement.getAsJsonObject();
            String coaCode = demandDetailJsonObject.get(BILL_DEMAND_CLIENT_COA_CODE).getAsString();

            Amount amount = Amount.builder()
                    .amount(demandDetailJsonObject.get(BILL_DEMAND_CLIENT_COA_AMOUNT).getAsBigDecimal())
                    .coaId(chartOfAccountService.getResolvedChartOfAccount(coaCode, data))
                    .fromBillingPeriod(taxPeriodFrom)
                    .toBillingPeriod(taxPeriodTo).build();

            amountList.add(amount);
        });
        return amountList;
    }
}
