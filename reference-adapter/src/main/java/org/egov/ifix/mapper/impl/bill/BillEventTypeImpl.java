package org.egov.ifix.mapper.impl.bill;

import java.time.Instant;
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
public class BillEventTypeImpl implements EventMapper {

	private static final String BILL = "bill";

	private static final String REFERANCE_ID = "consumerCode";

	private static final String BILL_DETAILS = "billDetails";

	private static final String BILL_ACCOUNT_DETAILS = "billAccountDetails";

	private static final String AMOUNT = "amount";

	private static final String FROMPERIOD = "fromPeriod";

	private static final String TOPERIOD = "toPeriod";

	private ApplicationConfiguration applicationConfiguration;

	@Autowired
	public BillEventTypeImpl(ApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
	}

	@Override
	public String getEventType() {
		return EventTypeEnum.BILL.toString();
	}

	@Override
	public List<FiscalEvent> transformData(JsonObject data) {
		JsonArray bills = data.getAsJsonObject(EventConstants.EVENT).getAsJsonArray(EventConstants.ENTITY);
		List<FiscalEvent> fiscalEvents = new ArrayList<FiscalEvent>();
		for (int i = 0; i < bills.size(); i++) {
			JsonObject bill = bills.get(i).getAsJsonObject().getAsJsonObject(BILL);

			FiscalEvent fiscalEvent = FiscalEvent.builder().tenantId(applicationConfiguration.getTenantId())
					.eventType(getEventType()).eventTime(Instant.now().toEpochMilli())
					.referenceId(bill.get(REFERANCE_ID).getAsString()).parentEventId(null).parentReferenceId(null)
					.amountDetails(getAmounts(bill)).build();

			fiscalEvents.add(fiscalEvent);

		}
		return fiscalEvents;
	}

	private List<Amount> getAmounts(JsonObject demand) {

		JsonArray billDetails = demand.getAsJsonArray(BILL_DETAILS);
		Long fromPeriod=0l;
		Long toPeriod=0l;
		List<Amount> amounts = new ArrayList<Amount>();
		for (int i = 0; i < billDetails.size(); i++) {

			 fromPeriod = billDetails.get(i).getAsJsonObject().get(FROMPERIOD).getAsLong();
			 toPeriod =  billDetails.get(i).getAsJsonObject().get(TOPERIOD).getAsLong();

			JsonArray billAccountDetails = billDetails.get(i).getAsJsonObject().getAsJsonArray(BILL_ACCOUNT_DETAILS);

			for (int j = 0; j < billAccountDetails.size(); j++) {
				JsonObject demanddetail = billAccountDetails.get(j).getAsJsonObject();

				Amount amount = Amount.builder().amount(demanddetail.get(AMOUNT).getAsBigDecimal()).coaId(null)
						.fromBillingPeriod(toPeriod).toBillingPeriod(toPeriod).build();

				amounts.add(amount);

			}
		}
		return amounts;

	}

}
