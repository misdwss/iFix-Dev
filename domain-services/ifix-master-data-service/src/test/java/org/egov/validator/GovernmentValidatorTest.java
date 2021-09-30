package org.egov.validator;

import org.egov.config.TestDataFormatter;
import org.egov.repository.GovernmentRepository;
import org.egov.web.models.Government;
import org.egov.web.models.GovernmentRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest()
class GovernmentValidatorTest {
    @Autowired
    private TestDataFormatter testDataFormatter;

    @InjectMocks
    GovernmentValidator governmentValidator;

    @Mock
    GovernmentRepository governmentRepository;

    private GovernmentRequest governmentRequest;

    private Government government;

    @BeforeAll
    public void init() throws IOException {
        governmentRequest = testDataFormatter.getGovernmentRequestData();

        government = governmentRequest.getGovernment();
    }

    @Test
    void validateGovernmentRequestData() {
        Mockito.doReturn(government).when(governmentRepository).findById(governmentRequest.getGovernment().getId());


    }

    @Test
    void validateGovernmentSearchRequestData() {
    }
}