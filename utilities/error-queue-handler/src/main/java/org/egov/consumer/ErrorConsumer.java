package org.egov.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.egov.dto.ErrorDataModel;
import org.egov.service.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ErrorConsumer {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ErrorService errorSynthesisService;

    @KafkaListener(topics = "${kafka.topics.ifix.adaptor.error}")
    public void consumeError(final String record) {

        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        ErrorDataModel errorDataModel = new Gson().fromJson(record, ErrorDataModel.class);

        errorSynthesisService.persistError(errorDataModel);


    }


}
