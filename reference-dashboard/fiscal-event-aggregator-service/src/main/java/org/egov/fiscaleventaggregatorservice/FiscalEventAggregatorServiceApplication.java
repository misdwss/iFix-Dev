package org.egov.fiscaleventaggregatorservice;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Data
public class FiscalEventAggregatorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FiscalEventAggregatorServiceApplication.class, args);
    }

}
