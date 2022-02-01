package org.egov.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestHeader;
import org.egov.config.FiscalEventPostProcessorConfig;
import org.egov.config.TestDataFormatter;
import org.egov.resposioty.ServiceRequestRepository;
import org.egov.tracer.model.CustomException;
import org.egov.models.Expenditure;
import org.egov.models.FiscalEventRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class ExpenditureUtilTest {

    @InjectMocks
    private ExpenditureUtil expenditureUtil;

    @Mock
    private FiscalEventPostProcessorConfig fiscalEventPostProcessorConfig;

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private TestDataFormatter testDataFormatter;

    private Expenditure expenditure;
    private FiscalEventRequest fiscalEventRequest;
    private JsonNode expenditureJsonNode;

    @BeforeAll
    void init() throws IOException {
        expenditureJsonNode = testDataFormatter.getExpenditureSearchResponse();
        expenditure = objectMapper.convertValue(expenditureJsonNode.get("expenditure").get(0), Expenditure.class);

        fiscalEventRequest = testDataFormatter.getFiscalEventValidatedData();
    }

    @Test
    void testGetExpenditureReferenceWithInvalidServiceResponse() {
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn("Fetch Result");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterExpenditureSearchPath())
                .thenReturn("Ifix Master Expenditure Search Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterExpenditureContextPath())
                .thenReturn("Ifix Master Expenditure Context Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterExpenditureHost()).thenReturn("localhost");
        assertThrows(CustomException.class,
                () -> this.expenditureUtil.getExpenditureReference("pb",
                        "910f4d23-fc63-48fb-a8fb-f0ad567b788a", new RequestHeader()));
        verify(this.serviceRequestRepository).fetchResult((String) any(), (Object) any());
        verify(this.fiscalEventPostProcessorConfig).getIfixMasterExpenditureContextPath();
        verify(this.fiscalEventPostProcessorConfig).getIfixMasterExpenditureHost();
        verify(this.fiscalEventPostProcessorConfig).getIfixMasterExpenditureSearchPath();
    }

    @Test
    void testGetExpenditureReferenceWithEmptyTenantId() {
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn("Fetch Result");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterExpenditureSearchPath())
                .thenReturn("Ifix Master Expenditure Search Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterExpenditureContextPath())
                .thenReturn("Ifix Master Expenditure Context Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterExpenditureHost()).thenReturn("localhost");
        assertTrue(this.expenditureUtil.getExpenditureReference("", "42", new RequestHeader()).isEmpty());
    }

    @Test
    void testGetExpenditureReferenceWithInvalidExpenditureId() {
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn("Fetch Result");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterExpenditureSearchPath())
                .thenReturn("Ifix Master Expenditure Search Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterExpenditureContextPath())
                .thenReturn("Ifix Master Expenditure Context Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterExpenditureHost()).thenReturn("localhost");
        assertTrue(this.expenditureUtil.getExpenditureReference("42", "", new RequestHeader()).isEmpty());
    }

    @Test
    void testGetExpenditureReferenceWithvalidResponseFromServiceRepo() {
        Map<String, Object> response = objectMapper.convertValue(expenditureJsonNode, new TypeReference<Map<String, Object>>() {
        });
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn(response);
        when(this.fiscalEventPostProcessorConfig.getIfixMasterExpenditureSearchPath())
                .thenReturn("Ifix Master Expenditure Search Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterExpenditureContextPath())
                .thenReturn("Ifix Master Expenditure Context Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterExpenditureHost()).thenReturn("localhost");
        List<Expenditure> expenditureList = expenditureUtil.getExpenditureReference("42",
                "910f4d23-fc63-48fb-a8fb-f0ad567b788a", fiscalEventRequest.getRequestHeader());

        assertNotNull(expenditureList);
        assertFalse(expenditureList.isEmpty());
    }
}

