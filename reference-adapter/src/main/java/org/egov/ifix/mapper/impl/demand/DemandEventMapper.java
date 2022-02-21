package org.egov.ifix.mapper.impl.demand;

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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.egov.ifix.utils.EventConstants.*;

@Component("DEMAND")
@Slf4j
public class DemandEventMapper implements EventMapper {

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
        return EventTypeEnum.DEMAND.toString();
    }

    /**
     * @param data
     * @return
     */
    @Override
    public List<FiscalEvent> transformData(JsonObject data) {
        log.info(EventConstants.LOG_INFO_PREFIX + "Transforming DEMAND Fiscal Event");

        List<FiscalEvent> fiscalEventList = new ArrayList<>();
        String clientProjectCode = data.getAsJsonObject(EventConstants.EVENT).get(EventConstants.PROJECT_ID)
                .getAsString();
        String iFixProjectId = projectService.getResolvedProjectId(clientProjectCode, data);
        JsonArray entityJA = data.getAsJsonObject(EVENT).getAsJsonArray(ENTITY);

        entityJA.forEach(entityJE -> {
            JsonArray demandJA = entityJE.getAsJsonObject().getAsJsonArray(DEMANDS);

            demandJA.forEach(demandJE -> {
                JsonObject demandJO = demandJE.getAsJsonObject();

                FiscalEvent fiscalEvent = FiscalEvent.builder()
                        .tenantId(applicationConfiguration.getTenantId())
                        .eventType(getEventType())
                        .eventTime(Instant.now().toEpochMilli())
                        .referenceId(demandJO.get(CONSUMER_CODE).getAsString())
                        .parentEventId(null)
                        .parentReferenceId(null)
                        .amountDetails(getAmountDetails(demandJO, data))
                        .projectId(iFixProjectId).build();

                fiscalEventList.add(fiscalEvent);
            });
        });

        log.info(EventConstants.LOG_INFO_PREFIX + "Transformed DEMAND fiscal event: "
                + dataWrapper.getFiscalEventDetailsInformation(fiscalEventList));
        return fiscalEventList;
    }

    /**
     * @param demand
     * @param data
     * @return
     */
    private List<Amount> getAmountDetails(JsonObject demand, JsonObject data) {
        List<Amount> amountList = new ArrayList<>();
        Long taxPeriodFrom = demand.get(BILL_DEMAND_FROM_BILLING_PERIOD).getAsLong();
        Long taxPeriodTo = demand.get(BILL_DEMAND_TO_BILLING_PERIOD).getAsLong();

        JsonArray demandDetailsJA = demand.getAsJsonArray(DEMAND_DETAILS);

        demandDetailsJA.forEach(demandDetailsJE -> {
            JsonObject demandDetailsJO = demandDetailsJE.getAsJsonObject();

            String coaCode = demandDetailsJO.get(BILL_DEMAND_CLIENT_COA_CODE).getAsString();

            Amount amount = Amount.builder()
                    .amount(demandDetailsJO.get(BILL_DEMAND_CLIENT_COA_AMOUNT).getAsBigDecimal())
                    .coaId(chartOfAccountService.getResolvedChartOfAccount(coaCode, data))
                    .fromBillingPeriod(taxPeriodFrom)
                    .toBillingPeriod(taxPeriodTo).build();

            amountList.add(amount);
        });

        return amountList;
    }
}
