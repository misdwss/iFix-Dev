package org.egov.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource("classpath:application_test.properties")
public class TestProperties {

    @Value("${test.data.package}")
    private String testDataPackage;

    @Value("${gov.create.request.data}")
    private String govCreateRequestData;

    @Value("${gov.create.response.data}")
    private String govCreateResponseData;

    @Value("${gov.search.request.data}")
    private String govSearchRequestData;

    @Value("${gov.search.response.data}")
    private String govSearchResponseData;

    @Value("${gov.create.request.data.headless}")
    private String govCreateRequestDataHeadless;



}
