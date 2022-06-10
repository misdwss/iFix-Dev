package org.egov.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SoapServiceRequestRepositoryTest {

    private SoapServiceRequestRepository soapServiceRequestRepository;

    String billReq = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "    <soap:Body>\n" +
            "        <GetBills xmlns=\"http://tempuri.org/\">\n" +
            "            <accountNo>3005754414</accountNo>\n" +
            "        </GetBills>\n" +
            "    </soap:Body>\n" +
            "</soap:Envelope>";

    String uri = "http://localhost:9093/fetch/pspcl/bill";

    @BeforeEach
    private void init() throws IOException {
        MockitoAnnotations.openMocks(this);
        soapServiceRequestRepository = new SoapServiceRequestRepository();
    }

    @Test
    void testFetchResult() {
        Object result = soapServiceRequestRepository.fetchResult(billReq, uri);
        String actual = result.toString();
        assertEquals("", actual);
    }
}

