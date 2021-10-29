package org.egov.ifix.consumer;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestHeader;
import org.egov.ifix.mapper.EventMapper;
import org.egov.ifix.models.FiscalEvent;
import org.egov.ifix.models.FiscalEventRequest;
import org.egov.ifix.models.FiscalEventResponse;
import org.egov.ifix.persistance.EventPostingDetail;
import org.egov.ifix.persistance.EventPostingDetailRepository;
import org.egov.ifix.service.PostEvent;
import org.egov.ifix.service.ProjectService;
import org.egov.ifix.utils.EventConstants;
import org.egov.ifix.utils.RequestHeaderUtil;
import org.egov.ifix.exception.GenericCustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EventTypeConsumer {

	private List<EventMapper> eventMappers;

	private Map<String, EventMapper> eventTypeMap = new HashMap<>();

	private PostEvent postEvent;

	private EventPostingDetailRepository eventPostingDetailRepository;

	private RequestHeaderUtil requestHeaderUtil;

	private ProjectService projectService;

	@Autowired
	public EventTypeConsumer(List<EventMapper> eventMappers, PostEvent postEvent,
			EventPostingDetailRepository eventPostingDetailRepository, RequestHeaderUtil requestHeaderUtil,
			ProjectService projectService) {
		this.eventMappers = Collections.unmodifiableList(eventMappers);
		this.postEvent = postEvent;
		this.eventPostingDetailRepository = eventPostingDetailRepository;
		this.requestHeaderUtil = requestHeaderUtil;
		this.projectService = projectService;
		initializeEventTypeMap();

	}

	private void initializeEventTypeMap() {
		for (EventMapper eventMapper : eventMappers)
			eventTypeMap.put(eventMapper.getEventType(), eventMapper);
	}

	@KafkaListener(topics = "${kafka.topics.ifix.adaptor.mapper}")
	public void listen(final String record) {

		process(record, null);
	}

	public void process(final String record, EventPostingDetail detail) {
		validateEventRequest(record);
		FiscalEventRequest fiscalEventRequest = new FiscalEventRequest();
		JsonObject jsonObject = JsonParser.parseString(record).getAsJsonObject();
		JsonObject eventJsonObject = jsonObject.getAsJsonObject(EventConstants.EVENT);

		try {
			RequestHeader header = requestHeaderUtil.polulateRequestHeader(jsonObject, new RequestHeader());
			fiscalEventRequest.setRequestHeader(header);

			EventMapper eventMapper = eventTypeMap.get(jsonObject.getAsJsonObject(EventConstants.EVENT)
					.get(EventConstants.EVENT_TYPE).getAsString());

			List<FiscalEvent> fiscalEvents = eventMapper.transformData(jsonObject);

			loop:
			for (FiscalEvent fiscalEvent : fiscalEvents) {
				String clientProjectCode = eventJsonObject.get(EventConstants.PROJECT_ID).getAsString();

				log.info(EventConstants.LOG_INFO_PREFIX + "MgramSeva project code " + clientProjectCode);

				String iFixProjectId = projectService.getProjectId(clientProjectCode, jsonObject);

				log.info(EventConstants.LOG_INFO_PREFIX + "IFix project id " + iFixProjectId);

				fiscalEvent.setProjectId(iFixProjectId);
				fiscalEventRequest.setFiscalEvent(fiscalEvent);

				if (detail == null) {
					detail = new EventPostingDetail();
					detail.setEventId(eventJsonObject.get(EventConstants.ID).getAsString());
					detail.setTenantId(fiscalEvent.getTenantId());
					detail.setReferenceId(fiscalEvent.getParentReferenceId());
					detail.setEventType(fiscalEvent.getEventType());
					detail.setCreatedDate(new Date());
					detail.setLastModifiedDate(new Date());
				}

				try {
					ResponseEntity<FiscalEventResponse> response = postEvent.post(fiscalEventRequest);

					if (response.getStatusCode().series().equals(HttpStatus.Series.SERVER_ERROR)
							|| response.getStatusCode().series().equals(HttpStatus.Series.CLIENT_ERROR)) {
						detail.setRecord(record);
					} else if (response.getStatusCode().series().equals(HttpStatus.Series.SUCCESSFUL)) {
						detail.setIfixEventId(response.getBody().getFiscalEvent().get(0).getId());
					}

					detail.setStatus(String.valueOf(response.getStatusCode().value()));
					detail.setRecord(record);
					eventPostingDetailRepository.save(detail);

				} catch (ResourceAccessException e) {
					log.error(e.getMessage(), e);
					detail.setStatus("500");
					detail.setError(extractedMessage(e));
					detail.setRecord(record);
					eventPostingDetailRepository.save(detail);
					break loop;

				} catch (RestClientException e) {
					log.error(e.getMessage(), e);
					detail.setStatus("400");
					detail.setError(extractedMessage(e));
					detail.setRecord(record);
					eventPostingDetailRepository.save(detail);
				}

			}
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
			EventPostingDetail errorDetail = new EventPostingDetail();
			errorDetail.setEventId(eventJsonObject.get(EventConstants.ID).getAsString());
			errorDetail.setTenantId(eventJsonObject.get(EventConstants.TENANT_ID).getAsString());
			errorDetail.setReferenceId("Not extracted");
			errorDetail.setEventType(eventJsonObject.get(EventConstants.EVENT_TYPE).getAsString());
			errorDetail.setCreatedDate(new Date());
			errorDetail.setLastModifiedDate(new Date());
			errorDetail.setStatus("400");
			errorDetail.setError(extractedMessage(e));
			errorDetail.setRecord(record);
			eventPostingDetailRepository.save(errorDetail);
		}

	}

	private String extractedMessage(Exception e) {
		String message = null;
		if (e.getMessage().length() > 4000) {
			message = e.getMessage().substring(0, 3999);
		} else {
			message = e.getMessage();
		}
		return message;
	}


	/**
	 * TODO: Validation of whole event request still pending
	 *
	 * @param eventRequest
	 */
	private void validateEventRequest(String eventRequest) {
		if (eventRequest != null) {
			JsonObject jsonObject = JsonParser.parseString(eventRequest).getAsJsonObject();

			if (!jsonObject.has(EventConstants.EVENT)) {
				throw new GenericCustomException(EventConstants.EVENT, "Event attribute is missing in payload");
			} else {
				Map<String, String> errorMap = new HashMap<>();
				JsonObject eventObject = jsonObject.getAsJsonObject(EventConstants.EVENT);

				if (!eventObject.has(EventConstants.EVENT_TYPE)) {
					errorMap.put(EventConstants.EVENT_TYPE, "Event Type is missing in payload");
				}

				if (!eventObject.has(EventConstants.PROJECT_ID) || eventObject.get(EventConstants.PROJECT_ID).isJsonNull()) {
					errorMap.put(EventConstants.PROJECT_ID, "Project id is missing in event payload");
				}

				if (!eventObject.has(EventConstants.ID)) {
					errorMap.put(EventConstants.ID, "Id is missing in event payload");
				}

				if (!eventObject.has(EventConstants.ENTITY)
						|| eventObject.getAsJsonArray(EventConstants.ENTITY).size() <= 0) {
					errorMap.put(EventConstants.ENTITY, "Event Entity is missing in payload");
				}

				if (!errorMap.isEmpty()) {
					throw new GenericCustomException(errorMap);
				}
			}
		}else {
			throw new GenericCustomException(EventConstants.PAYLOAD, "Invalid payload");
		}
	}
}
