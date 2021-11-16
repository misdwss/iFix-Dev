package org.egov.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestHeader;
import org.egov.config.FiscalEventConfiguration;
import org.egov.config.TestDataFormatter;
import org.egov.repository.ServiceRequestRepository;
import org.egov.web.models.FiscalEventRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Slf4j
class ProjectUtilTest {

    @Autowired
    private TestDataFormatter testDataFormatter;

    @Mock
    private FiscalEventConfiguration fiscalEventConfiguration;
    @Mock
    private ServiceRequestRepository serviceRequestRepository;
    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProjectUtil projectUtil;

    private FiscalEventRequest fiscalEventRequest;
    private JsonNode projectSearchResponse;

    @BeforeAll
    public void init() throws IOException {
        fiscalEventRequest = testDataFormatter.getFiscalEventPushRequestData();
        projectSearchResponse = testDataFormatter.getProjectSearchResponse();
    }

    @Test
    void testValidateProjectId() {
        RequestHeader requestHeader = new RequestHeader();
        assertFalse(this.projectUtil.validateProjectId(requestHeader, new HashSet<String>(), "pb").isPresent());
    }

    @Test
    void testValidateProjectIdWithoutRequestHeader() {
        assertFalse(this.projectUtil.validateProjectId(null, new HashSet<String>(), "pb").isPresent());
    }

    @Test
    void testValidateProjectIdWithValidDetails() throws IllegalArgumentException {
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn("Fetch Result");
        when(this.fiscalEventConfiguration.getIfixMasterProjectSearchPath()).thenReturn("Ifix Master Project Search Path");
        when(this.fiscalEventConfiguration.getIfixMasterProjectContextPath())
                .thenReturn("Ifix Master Project Context Path");
        when(this.fiscalEventConfiguration.getIfixMasterProjectHost()).thenReturn("localhost");
        RequestHeader requestHeader = fiscalEventRequest.getRequestHeader();

        HashSet<String> projectIds = new HashSet<String>();
        projectIds.add(fiscalEventRequest.getFiscalEvent().get(0).getProjectId());
        assertTrue(this.projectUtil.validateProjectId(requestHeader, projectIds, fiscalEventRequest.getFiscalEvent().get(0).getTenantId()).isPresent());

        verify(this.serviceRequestRepository).fetchResult((String) any(), (Object) any());
        verify(this.objectMapper).convertValue((Object) any(), (Class<Object>) any());
        verify(this.fiscalEventConfiguration).getIfixMasterProjectContextPath();
        verify(this.fiscalEventConfiguration).getIfixMasterProjectHost();
        verify(this.fiscalEventConfiguration).getIfixMasterProjectSearchPath();
    }

    @Test
    void testValidateProjectIdWithNullTenantId() throws IllegalArgumentException {
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn("Fetch Result");
        when(this.fiscalEventConfiguration.getIfixMasterProjectSearchPath()).thenReturn("Ifix Master Project Search Path");
        when(this.fiscalEventConfiguration.getIfixMasterProjectContextPath())
                .thenReturn("Ifix Master Project Context Path");
        when(this.fiscalEventConfiguration.getIfixMasterProjectHost()).thenReturn("localhost");
        RequestHeader requestHeader = new RequestHeader();

        HashSet<String> stringSet = new HashSet<String>();
        stringSet.add("ba0298d1-0d32-4da5-9c5c-1dccf2c65e92");
        assertFalse(this.projectUtil.validateProjectId(requestHeader, stringSet, null).isPresent());
    }

    @Test
    void testValidateProjectIdWithEmptyTenantId() throws IllegalArgumentException {
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn("Fetch Result");
        when(this.fiscalEventConfiguration.getIfixMasterProjectSearchPath()).thenReturn("Ifix Master Project Search Path");
        when(this.fiscalEventConfiguration.getIfixMasterProjectContextPath())
                .thenReturn("Ifix Master Project Context Path");
        when(this.fiscalEventConfiguration.getIfixMasterProjectHost()).thenReturn("localhost");
        RequestHeader requestHeader = new RequestHeader();

        HashSet<String> stringSet = new HashSet<String>();
        stringSet.add("ba0298d1-0d32-4da5-9c5c-1dccf2c65e92");
        assertFalse(this.projectUtil.validateProjectId(requestHeader, stringSet, "").isPresent());
    }
}
