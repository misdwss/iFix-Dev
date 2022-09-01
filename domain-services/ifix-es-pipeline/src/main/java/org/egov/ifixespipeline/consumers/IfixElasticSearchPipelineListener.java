package org.egov.ifixespipeline.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.ifixespipeline.models.Ancestry;
import org.egov.ifixespipeline.models.FiscalEventRequest;
import org.egov.ifixespipeline.models.ProducerPOJO;
import org.egov.ifixespipeline.producer.Producer;
import org.egov.ifixespipeline.service.FiscalDataEnrichmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IfixElasticSearchPipelineListener {

    @Autowired
    private FiscalDataEnrichmentService fiscalDataEnrichmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Producer producer;

    @Value("${fiscal.event.es.push.topic}")
    private String indexFiscalEventsTopic;


    /**
     * Kafka consumer
     *
     * @param record
     * @param topic
     */
    @KafkaListener(topics = { "${fiscal.event.kafka.push.topic}"})
    public void listen(HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            FiscalEventRequest incomingData = objectMapper.convertValue(record, FiscalEventRequest.class);
            fiscalDataEnrichmentService.enrichFiscalData(incomingData);
            producer.push(indexFiscalEventsTopic, incomingData);
        }catch(Exception e) {
            log.error("Exception while reading from the queue: ", e);
        }
    }
}
