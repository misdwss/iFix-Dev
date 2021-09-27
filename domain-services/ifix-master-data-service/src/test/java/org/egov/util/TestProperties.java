package org.egov.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource("classpath:application_test.properties")
public class TestProperties {

    @Value("${test.data.base.package}")
    private String testDataBasePackage;

    @Value("${gov.create.request.data}")
    private String govCreateRequestData;

    @Value("${gov.response.data}")
    private String govResponseData;

}
