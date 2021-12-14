package org.egov.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
<<<<<<< HEAD
import org.egov.common.contract.request.RequestHeader;
=======
>>>>>>> f070c61465b100be594b1916109e464860bcc3cb
import org.egov.config.FiscalEventConfiguration;
import org.egov.config.TestDataFormatter;
import org.egov.repository.ServiceRequestRepository;
import org.egov.tracer.model.CustomException;
<<<<<<< HEAD
import org.egov.web.models.Amount;
import org.egov.web.models.FiscalEvent;
=======
>>>>>>> f070c61465b100be594b1916109e464860bcc3cb
import org.egov.web.models.FiscalEventRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
<<<<<<< HEAD
import org.junit.jupiter.api.extension.ExtendWith;
=======
>>>>>>> f070c61465b100be594b1916109e464860bcc3cb
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
<<<<<<< HEAD
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
=======
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;


>>>>>>> f070c61465b100be594b1916109e464860bcc3cb
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Slf4j
class CoaUtilTest {

    @Autowired
    private TestDataFormatter testDataFormatter;

    @Mock
    private FiscalEventConfiguration fiscalEventConfiguration;
    @Mock
    private ServiceRequestRepository serviceRequestRepository;
    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private CoaUtil coaUtil;

    private FiscalEventRequest fiscalEventRequest;
    private JsonNode validCOAResponse;

    @BeforeAll
    public void init() throws IOException {
        fiscalEventRequest = testDataFormatter.getFiscalEventPushRequestData();
        validCOAResponse = testDataFormatter.getCOASearchResponse();
    }

    @Test
<<<<<<< HEAD
    void testGetCOAIdsFromCOAServiceWithEmptyCOAIds() {
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn("Fetch Result");
        when(this.fiscalEventConfiguration.getIfixMasterCoaSearchPath()).thenReturn("Ifix Master Coa Search Path");
        when(this.fiscalEventConfiguration.getIfixMasterCoaContextPath()).thenReturn("Ifix Master Coa Context Path");
        when(this.fiscalEventConfiguration.getIfixMasterCoaHost()).thenReturn("localhost");
        RequestHeader requestHeader = fiscalEventRequest.getRequestHeader();
        assertThrows(CustomException.class,
                () -> this.coaUtil.getCOAIdsFromCOAService(requestHeader, new HashSet<String>(), "pb"));
        verify(this.serviceRequestRepository).fetchResult((String) any(), (Object) any());
        verify(this.fiscalEventConfiguration).getIfixMasterCoaContextPath();
        verify(this.fiscalEventConfiguration).getIfixMasterCoaHost();
        verify(this.fiscalEventConfiguration).getIfixMasterCoaSearchPath();
    }

    @Test
    void testGetCOAIdsFromCOAServiceWithEmptyTenantId() {
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn("Fetch Result");
        when(this.fiscalEventConfiguration.getIfixMasterCoaSearchPath()).thenReturn("Ifix Master Coa Search Path");
        when(this.fiscalEventConfiguration.getIfixMasterCoaContextPath()).thenReturn("Ifix Master Coa Context Path");
        when(this.fiscalEventConfiguration.getIfixMasterCoaHost()).thenReturn("localhost");
        RequestHeader requestHeader = fiscalEventRequest.getRequestHeader();
        assertThrows(CustomException.class,
                () -> this.coaUtil.getCOAIdsFromCOAService(requestHeader, new HashSet<String>(), ""));
        verify(this.serviceRequestRepository).fetchResult((String) any(), (Object) any());
        verify(this.fiscalEventConfiguration).getIfixMasterCoaContextPath();
        verify(this.fiscalEventConfiguration).getIfixMasterCoaHost();
        verify(this.fiscalEventConfiguration).getIfixMasterCoaSearchPath();
    }

    @Test
    public void testGetCOAIdsFromCOAServiceWithInvalidResponse() {
        doReturn(objectMapper.createObjectNode()).when(serviceRequestRepository).fetchResult(any(), any());
        Set<String> coaIds = new HashSet<>();
        String tenantId = fiscalEventRequest.getFiscalEvent().get(0).getTenantId();
        for (FiscalEvent fiscalEvent : fiscalEventRequest.getFiscalEvent()) {
            for (Amount amount : fiscalEvent.getAmountDetails()) {
                coaIds.add(amount.getCoaId());
            }
        }
        assertThrows(CustomException.class,
                () -> coaUtil.getCOAIdsFromCOAService(fiscalEventRequest.getRequestHeader(), coaIds, tenantId));
=======
    public void testGetCOAIdsFromCOAServiceWithInvalidResponse() {
        doReturn(objectMapper.createObjectNode()).when(serviceRequestRepository).fetchResult(any(), any());
        assertThrows(CustomException.class,
                () -> coaUtil.getCOAIdsFromCOAService(fiscalEventRequest.getRequestHeader(),
                        fiscalEventRequest.getFiscalEvent()));
>>>>>>> f070c61465b100be594b1916109e464860bcc3cb
    }

    @Test
    public void testGetCOAIdsFromCOAServiceWithValidResponse() {
        Map<String, Object> map = objectMapper.convertValue(validCOAResponse, new TypeReference<Map<String, Object>>() {
        });
        doReturn(map).when(serviceRequestRepository).fetchResult(any(), any());
<<<<<<< HEAD
        Set<String> coaIds = new HashSet<>();
        String tenantId = fiscalEventRequest.getFiscalEvent().get(0).getTenantId();
        for (FiscalEvent fiscalEvent : fiscalEventRequest.getFiscalEvent()) {
            for (Amount amount : fiscalEvent.getAmountDetails()) {
                coaIds.add(amount.getCoaId());
            }
        }
        List<String> validCOAIds = coaUtil.getCOAIdsFromCOAService(fiscalEventRequest.getRequestHeader(), coaIds, tenantId);
=======
        List<String> validCOAIds = coaUtil.getCOAIdsFromCOAService(fiscalEventRequest.getRequestHeader(),
                fiscalEventRequest.getFiscalEvent());
>>>>>>> f070c61465b100be594b1916109e464860bcc3cb
        assertTrue(validCOAIds.size() > 0);
    }

}
