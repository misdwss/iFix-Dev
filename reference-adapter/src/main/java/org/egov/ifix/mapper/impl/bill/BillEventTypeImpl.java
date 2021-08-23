package org.egov.ifix.mapper.impl.bill;

import java.util.List;
import java.util.Map;

import org.egov.ifix.mapper.EventMapper;
import org.egov.ifix.models.EventTypeEnum;
import org.egov.ifix.models.FiscalEvent;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

@Component
public class BillEventTypeImpl implements EventMapper{

	@Override
	public List<FiscalEvent> transformData(JsonObject data) {
		System.out.println("BillEventTypeImpl---");
		return null;
	}

	@Override
	public String getEventType() {
		// TODO Auto-generated method stub
		return EventTypeEnum.BILL.toString();
	}

}
