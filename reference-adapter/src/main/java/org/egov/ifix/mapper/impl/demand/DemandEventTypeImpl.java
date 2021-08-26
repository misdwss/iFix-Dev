package org.egov.ifix.mapper.impl.demand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.egov.ifix.mapper.EventMapper;
import org.egov.ifix.models.Amount;
import org.egov.ifix.models.EventTypeEnum;
import org.egov.ifix.models.FiscalEvent;
import org.egov.ifix.utils.ApplicationConfiguration;
import org.egov.ifix.utils.EventConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DemandEventTypeImpl implements EventMapper {

	private static final String DEMAND = "Demands";

	private static final String REFERANCE_ID = "consumerCode";

	private static final String DEMAND_DETAILS = "demandDetails";

	private static final String TAX_AMOUNT = "taxAmount";

	private static final String TAX_PERIOD_FROM = "taxPeriodFrom";

	private static final String TAX_PERIOD_TO = "taxPeriodTo";

	private ApplicationConfiguration applicationConfiguration;

	@Autowired
	public DemandEventTypeImpl(ApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
	}

	@Override
	public List<FiscalEvent> transformData(JsonObject data) {
		JsonArray demands = data.getAsJsonObject(EventConstants.EVENT).getAsJsonArray(EventConstants.ENTITY);
		List<FiscalEvent> fiscalEvents = new ArrayList<FiscalEvent>();
		for (int i = 0; i < demands.size(); i++) {
			JsonObject demand = demands.get(i).getAsJsonObject().getAsJsonObject(DEMAND);

			FiscalEvent fiscalEvent = FiscalEvent.builder().tenantId(applicationConfiguration.getTenantId())
					.projectId("3d9ef18a-361a-40cf-b142-dd6f998e1ac5").eventType(getEventType()).eventTime(null)
					.referenceId(demand.get(REFERANCE_ID).getAsString()).parentEventId(null).parentReferenceId(null)
					.amountDetails(getAmounts(demand)).build();

			fiscalEvents.add(fiscalEvent);

		}
		return fiscalEvents;
	}

	@Override
	public String getEventType() {
		return EventTypeEnum.DEMAND.toString();
	}

	private List<Amount> getAmounts(JsonObject demand) {
		Long taxPeriodFrom = demand.get(TAX_PERIOD_FROM).getAsLong();
		Long taxPeriodTo = demand.get(TAX_PERIOD_TO).getAsLong();

		JsonArray demandDetails = demand.getAsJsonArray(DEMAND_DETAILS);
		List<Amount> amounts = new ArrayList<Amount>();
		for (int i = 0; i < demandDetails.size(); i++) {
			JsonObject demanddetail = demandDetails.get(i).getAsJsonObject();

			Amount amount = Amount.builder().
					amount(demanddetail.get(TAX_AMOUNT).getAsBigDecimal()).
					coaId(null).
					fromBillingPeriod(taxPeriodFrom).
					toBillingPeriod(taxPeriodTo).build();

			amounts.add(amount);

		}
		return amounts;

	}

}
