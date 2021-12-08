package org.egov.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class PropertiesConfiguration {

    @Value("${app.timezone}")
    private String timeZone;
}
