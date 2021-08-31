package org.egov.ifix.mapper.impl.receipt;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
public class ReceiptEventTypeImpl implements EventMapper {

	private static final String PAYMENT = "Payment";

	private static final String REFERANCE_ID = "id";

	private static final String PAYMENT_DETAILS = "paymentDetails";

	private static final String TAX_AMOUNT = "amount";

	private static final String TAX_PERIOD_FROM = "fromPeriod";

	private static final String TAX_PERIOD_TO = "toPeriod";

	private static final String BILL = "bill";

	private static final String BILL_ACCOUNT_DETAILS = "billAccountDetails";

	private ApplicationConfiguration applicationConfiguration;

	@Autowired
	public ReceiptEventTypeImpl(ApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
	}

	@Override
	public String getEventType() {
		return EventTypeEnum.RECEIPT.toString();
	}

	@Override
	public List<FiscalEvent> transformData(JsonObject data) {
		log.info("Receipt event impl executing");
		JsonArray payments = data.getAsJsonObject(EventConstants.EVENT).getAsJsonArray(EventConstants.ENTITY);
		List<FiscalEvent> fiscalEvents = new ArrayList<FiscalEvent>();
		for (int i = 0; i < payments.size(); i++) {
			JsonObject payment = payments.get(i).getAsJsonObject().getAsJsonObject(PAYMENT);

			FiscalEvent fiscalEvent = FiscalEvent.builder().tenantId(applicationConfiguration.getTenantId())
					.eventType(getEventType()).eventTime(Instant.now().toEpochMilli())
					.referenceId(payment.get(REFERANCE_ID).getAsString()).parentEventId(null).parentReferenceId(null)
					.amountDetails(getAmounts(payment)).build();

			fiscalEvents.add(fiscalEvent);

		}
		return fiscalEvents;
	}

	private List<Amount> getAmounts(JsonObject payment) {

		JsonObject paymentDetails = payment.getAsJsonArray(PAYMENT_DETAILS).get(0).getAsJsonObject();

		JsonArray billDetails = paymentDetails.getAsJsonObject(BILL).getAsJsonArray("billDetails");

		List<Amount> amounts = new ArrayList<Amount>();

		for (int i = 0; i < billDetails.size(); i++) {
			JsonObject billDetail = billDetails.get(i).getAsJsonObject();
			Long taxPeriodFrom = billDetail.get(TAX_PERIOD_FROM).getAsLong();
			Long taxPeriodTo = billDetail.get(TAX_PERIOD_TO).getAsLong();

			JsonArray billAccDetails = billDetail.getAsJsonArray(BILL_ACCOUNT_DETAILS);

			for (int j = 0; j < billAccDetails.size(); j++) {
				JsonObject billAccDetail = billAccDetails.get(j).getAsJsonObject();
				Amount amount = Amount.builder().amount(billAccDetail.get(TAX_AMOUNT).getAsBigDecimal()).coaId(null)
						.fromBillingPeriod(taxPeriodFrom).toBillingPeriod(taxPeriodTo).build();

				amounts.add(amount);
			}

		}
		return amounts;

	}
}
