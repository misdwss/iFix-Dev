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

import java.util.*;

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

    @Value("${fiscal.event.kafka.push.topic}")
    private String fiscalEventsNewRecordsTopic;

    private Long noOfRecordsMigrated = 0l;

    private HashSet<String> listOfUniqueIds;

    /**
     * Kafka consumer
     *
     * @param record
     * @param topic
     */
    @KafkaListener(topics = { "${fiscal.event.kafka.push.topic}", "${fiscal.event.migration.origin.push.topic}"})
    public void listen(HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            FiscalEventRequest incomingData = objectMapper.convertValue(record, FiscalEventRequest.class);

            // Enrich hierarchy map in only new records. In case of migration records, skip enrichment as they are already getting enriched in migration toolkit.
            if(topic.equalsIgnoreCase(fiscalEventsNewRecordsTopic))
                fiscalDataEnrichmentService.enrichFiscalData(incomingData);
            else {
                noOfRecordsMigrated += 1;
                listOfUniqueIds.add(incomingData.getFiscalEvent().getId());
            }

            fiscalDataEnrichmentService.enrichComputedFields(incomingData);

            producer.push(indexFiscalEventsTopic, incomingData);
            log.info("Total no of migration records till now: " + listOfUniqueIds.size());
        }catch(Exception e) {
            log.error("Exception while reading from the queue: ", e);
        }
    }
}
