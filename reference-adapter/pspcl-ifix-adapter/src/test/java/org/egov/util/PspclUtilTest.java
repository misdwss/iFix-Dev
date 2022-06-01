package org.egov.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.egov.config.PspclIfixAdapterConfiguration;
import org.egov.repository.SoapServiceRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class PspclUtilTest {

    @InjectMocks
    private PspclUtil pspclUtil;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private XmlMapper xmlMapper;

    @Mock
    private PspclIfixAdapterConfiguration pspclIfixAdapterConfiguration;

    @Mock
    private PspclIfixAdapterUtil pspclIfixAdapterUtil;

    @Mock
    private SoapServiceRequestRepository soapServiceRequestRepository;


    @Test
    void testGetBillsFromPspcl() {
        assertTrue((new PspclUtil()).getBillsFromPspcl("3005754414").isEmpty());
    }

    @Test
    void testGetPaymentsFromPspcl() {
        assertTrue((new PspclUtil()).getPaymentsFromPspcl("3005754414").isEmpty());
    }
}

