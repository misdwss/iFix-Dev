package org.egov.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class SoapServiceRequestRepositoryTest {

    @InjectMocks
    private SoapServiceRequestRepository soapServiceRequestRepository;

    @Test
    void testFetchResult() {
        assertEquals("", this.soapServiceRequestRepository.fetchResult("Req", "Uri"));
        assertEquals("", this.soapServiceRequestRepository.fetchResult("Req", "Uri"));
        assertEquals("", this.soapServiceRequestRepository.fetchResult("", "Uri"));
        assertEquals("", this.soapServiceRequestRepository.fetchResult(null, "Uri"));
        assertEquals("", this.soapServiceRequestRepository.fetchResult("Req", ""));
    }
}

