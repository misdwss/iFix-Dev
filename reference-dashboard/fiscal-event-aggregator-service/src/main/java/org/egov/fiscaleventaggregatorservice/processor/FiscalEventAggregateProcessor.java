package org.egov.fiscaleventaggregatorservice.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FiscalEventAggregateProcessor implements ApplicationRunner {

    @Autowired
    private DruidDataQueryProcessor druidDataQueryProcessor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        druidDataQueryProcessor.fetchFiscalEventFromDruid();
    }
}
