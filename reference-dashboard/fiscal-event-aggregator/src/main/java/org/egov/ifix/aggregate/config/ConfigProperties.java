package org.egov.ifix.aggregate.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class ConfigProperties {

    @Value("${app.timezone}")
    private String timeZone;

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

    @Value("${fiscal.event.datasource}")
    private String fiscalEventDataSource;

    @Value("${druid.connect.protocol}")
    private String druidConnectProtocol;

    @Value("${druid.connect.port}")
    private String druidConnectPort;

    @Value("${fiscal.period}")
    private String fiscalPeriod;

    @Value("${department.hierarchy.level}")
    private String departmentHierarchyLevel;
}
