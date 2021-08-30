package org.egov.ifix.consumer;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.egov.common.contract.request.RequestHeader;
import org.egov.ifix.mapper.EventMapper;
import org.egov.ifix.models.FiscalEvent;
import org.egov.ifix.models.FiscalEventRequest;
import org.egov.ifix.models.FiscalEventResponse;
import org.egov.ifix.persistance.EventPostingDetail;
import org.egov.ifix.persistance.EventPostingDetailRepository;
import org.egov.ifix.service.PostEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EventTypeConsumer {

	private static final String MSG_ID = "msgId";

	private static final String VERSION = "version";

	private static final String REQUEST_HEADER = "requestHeader";

	private static final String EVENT_TYPE = "eventType";

	private static final String EVENT = "event";

	private List<EventMapper> eventMappers;

	private Map<String, EventMapper> eventTypeMap = new HashMap<>();

	private PostEvent postEvent;

	private EventPostingDetailRepository eventPostingDetailRepository;

	@Autowired
	public EventTypeConsumer(List<EventMapper> eventMappers, PostEvent postEvent,
			EventPostingDetailRepository eventPostingDetailRepository) {
		this.eventMappers = Collections.unmodifiableList(eventMappers);
		this.postEvent = postEvent;
		this.eventPostingDetailRepository = eventPostingDetailRepository;
		initializeEventTypeMap();

	}

	private void initializeEventTypeMap() {
		for (EventMapper eventMapper : eventMappers)
			eventTypeMap.put(eventMapper.getEventType(), eventMapper);
	}

	@KafkaListener(topics = "${kafka.topics.ifix.adaptor.mapper}")
	public void listen(final String record) {
		log.info("Received Message in group foo: " + record);
		JsonObject jsonObject = JsonParser.parseString(record).getAsJsonObject();
		EventMapper eventMapper = eventTypeMap.get(jsonObject.getAsJsonObject(EVENT).get(EVENT_TYPE).getAsString());
		List<FiscalEvent> fiscalEvents = eventMapper.transformData(jsonObject);
		FiscalEventRequest request = new FiscalEventRequest();
		RequestHeader header = new RequestHeader();
		if (jsonObject.get(REQUEST_HEADER) != null) {
			JsonElement requestHeader = jsonObject.get(REQUEST_HEADER);
			JsonObject requestJsonObject = requestHeader.getAsJsonObject();
			if (requestJsonObject.get(VERSION) != null) {
				header.setVersion(requestJsonObject.get(VERSION).getAsString());
			}
			if (requestJsonObject.get(MSG_ID) != null) {
				header.setMsgId(requestJsonObject.get(MSG_ID).getAsString());
			}
			header.setTs(Instant.now().toEpochMilli());
		}

		request.setRequestHeader(header);

		loop: for (FiscalEvent event : fiscalEvents) {

		 
			if (jsonObject.getAsJsonObject(EVENT).get("projectId")!= null) {
				event.setProjectId(jsonObject.getAsJsonObject(EVENT).get("projectId").getAsString());
				 
			}

			request.setFiscalEvent(event);
			log.info(event.getProjectId());

			try {
				ResponseEntity<FiscalEventResponse> response = postEvent.post(request);
				if (response.getStatusCode().series().equals(HttpStatus.Series.SERVER_ERROR)) {

					EventPostingDetail detail = new EventPostingDetail(null, event.getTenantId(),
							event.getReferenceId(), event.getEventType(), response.getStatusCode().toString(), null,
							record, new Date(), new Date());
					eventPostingDetailRepository.save(detail);
				} else {
					EventPostingDetail detail = new EventPostingDetail(null, event.getTenantId(),
							event.getReferenceId(), event.getEventType(), response.getStatusCode().toString(), null,
							record, new Date(), new Date());
					eventPostingDetailRepository.save(detail);
				}

			} catch (RuntimeException e) {
				String message = null;
				if (e.getMessage().length() > 4000) {
					message = e.getMessage().substring(0, 3999);
				} else {
					message = e.getMessage();
				}
				EventPostingDetail detail = new EventPostingDetail(null, event.getTenantId(), event.getReferenceId(),
						event.getEventType(), "400", message, record, new Date(), new Date());
				eventPostingDetailRepository.save(detail);
				break loop;
			}

		}

	}

}
