package org.egov.fiscaleventaggregatorservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class ConfigProperties {

    @Value("${druid.host}")
    private String druidHost;

    @Value("${druid.endPoint}")
    private String druidEndPoint;

    @Value("${spring.datasource.driver-class-name}")
    private String postgresDatasourceDriverClassName;

    @Value("${spring.datasource.url}")
    private String postgresDatasourceUrl;

    @Value("${spring.datasource.username}")
    private String postgresDatasourceUsername;

    @Value("${spring.datasource.password}")
    private String postgresDatasourcePassword;
}
