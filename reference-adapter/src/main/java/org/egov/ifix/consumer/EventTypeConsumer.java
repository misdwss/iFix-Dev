package org.egov.ifix.consumer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.ifix.mapper.EventMapper;
import org.egov.ifix.models.FiscalEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EventTypeConsumer {
	
	private List<EventMapper> eventMappers;

	private Map<String, EventMapper> eventTypeMap = new HashMap<>();
		

	@Autowired
	public EventTypeConsumer(List<EventMapper> eventMappers) {
		this.eventMappers = Collections.unmodifiableList(eventMappers);
		initializeEventTypeMap();

	}
	
	private void initializeEventTypeMap() {
		for (EventMapper eventMapper : eventMappers) 
			eventTypeMap.put(eventMapper.getEventType(), eventMapper);
	}
	
	@KafkaListener(topics = "${kafka.topics.ifix.adaptor.mapper}")
	public void listen(final String record)  {
	    log.info("Received Message in group foo: " + record);
	    JsonObject jsonObject = JsonParser.parseString(record).getAsJsonObject();
	    EventMapper eventMapper = eventTypeMap.get(jsonObject.getAsJsonObject("event").get("eventType").getAsString());
	    List<FiscalEvent> fiscalEvents = eventMapper.transformData(jsonObject);
	    
	    Gson gson = new Gson();
	    String json = gson.toJson(fiscalEvents);
	    log.info(json);
	}

}
