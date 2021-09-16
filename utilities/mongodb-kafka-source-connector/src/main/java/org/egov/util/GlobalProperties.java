package org.egov.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class GlobalProperties {

    @Value("${mongo.dereference.collection.name}")
    private String mongoDereferenceCollectionName;

    @Value("${fiscal.event.dereference.topic}")
    private String fiscalEventDereferenceTopic;
}
