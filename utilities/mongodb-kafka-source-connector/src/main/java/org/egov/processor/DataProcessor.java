package org.egov.processor;

import com.mongodb.client.FindIterable;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.egov.producer.CustomKafkaProducer;
import org.egov.repository.DereferenceRepository;
import org.egov.util.GlobalProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataProcessor {

    @Autowired
    DereferenceRepository dereferenceRepository;

    @Autowired
    CustomKafkaProducer customKafkaProducer;

    @Autowired
    GlobalProperties globalProperties;

    /**
     * @return
     */
    public boolean processAndPublishDereferenceFiscalEvent() {
        try {
            FindIterable<Document> iterableDocument = dereferenceRepository.findAllFiscalEvent();

            if (iterableDocument != null && iterableDocument.iterator() != null) {
                for (Document document : iterableDocument) {
                    String jsonDocument = document.toJson();

                    customKafkaProducer.push(globalProperties.getKafkaTopic(), document);
                }

                return true;
            }
        } catch (Exception e) {
            log.error(">>>>> Error while processing fiscal event dereference data", e);
        }

        return false;
    }

}
