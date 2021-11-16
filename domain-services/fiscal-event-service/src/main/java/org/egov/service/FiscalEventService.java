package org.egov.service;


import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestHeader;
import org.egov.config.FiscalEventConfiguration;
import org.egov.producer.Producer;
import org.egov.repository.FiscalEventRepository;
import org.egov.util.FiscalEventMapperUtil;
import org.egov.validator.FiscalEventValidator;
import org.egov.web.models.Criteria;
import org.egov.web.models.FiscalEvent;
import org.egov.web.models.FiscalEventGetRequest;
import org.egov.web.models.FiscalEventRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class FiscalEventService {

    @Autowired
    private FiscalEventValidator validator;

    @Autowired
    private FiscalEventEnrichmentService enricher;

    @Autowired
    private Producer producer;

    @Autowired
    private FiscalEventConfiguration eventConfiguration;

    @Autowired
    private FiscalEventRepository eventRepository;

    @Autowired
    private FiscalEventMapperUtil mapperUtil;


    /**
     * Validate , enrich and push the fiscal Event request to topic
     *
     * @param fiscalEventRequest
     * @return
     */
    public FiscalEventRequest fiscalEventsV1PushPost(FiscalEventRequest fiscalEventRequest) {
        validator.validateFiscalEventPushPost(fiscalEventRequest);
        enricher.enrichFiscalEventPushPost(fiscalEventRequest);

        if (fiscalEventRequest.getFiscalEvent() != null && !fiscalEventRequest.getFiscalEvent().isEmpty()) {
            RequestHeader requestHeader = fiscalEventRequest.getRequestHeader();
            for (FiscalEvent fiscalEvent : fiscalEventRequest.getFiscalEvent()) {
                ObjectNode fiscalEventRequestNode = JsonNodeFactory.instance.objectNode();
                fiscalEventRequestNode.putPOJO("requestHeader",requestHeader);
                fiscalEventRequestNode.putPOJO("fiscalEvent",fiscalEvent);

                producer.push(eventConfiguration.getFiscalPushRequest(), fiscalEventRequestNode);
            }
        }
        return fiscalEventRequest;
    }

    /**
     * Validate the request, search based on the criteria
     *
     * @param fiscalEventGetRequest
     * @return
     */
    public List<FiscalEvent> fiscalEventsV1SearchPost(FiscalEventGetRequest fiscalEventGetRequest) {
        validator.validateFiscalEventSearchPost(fiscalEventGetRequest);
        Criteria searchCriteria = fiscalEventGetRequest.getCriteria();

        if ((searchCriteria.getIds() == null || searchCriteria.getIds().isEmpty()) && StringUtils.isBlank(searchCriteria.getEventType())
                && StringUtils.isBlank(searchCriteria.getTenantId()) && searchCriteria.getFromEventTime() == null
                && searchCriteria.getToEventTime() == null && (searchCriteria.getReferenceId() == null || searchCriteria.getReferenceId().isEmpty())) {
            return Collections.emptyList();
        }

        List<Object> dereferencedFiscalEvents = eventRepository.searchFiscalEvent(searchCriteria);

        if (dereferencedFiscalEvents == null || dereferencedFiscalEvents.isEmpty())
            return Collections.emptyList();

        List<FiscalEvent> fiscalEvents = mapperUtil.mapDereferencedFiscalEventToFiscalEvent(dereferencedFiscalEvents);

        return fiscalEvents;
    }
}
