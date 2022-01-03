package org.egov.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestHeader;
import org.egov.config.IfixDepartmentEntityConfig;
import org.egov.config.TestDataFormatter;
import org.egov.repository.ServiceRequestRepository;
import org.egov.tracer.model.CustomException;
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
class GovernmentUtilTest {

    @InjectMocks
    private GovernmentUtil governmentUtil;

    @Mock
    private IfixDepartmentEntityConfig ifixDepartmentEntityConfig;

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Spy
    private ObjectMapper objectMapper;

    @Autowired
    private TestDataFormatter testDataFormatter;

    private JsonNode govSearchResNode;

    @BeforeAll
    void init() throws IOException {
        govSearchResNode = testDataFormatter.getValidGovernmentSearchResponse();
    }

    @Test
    void testGetGovernmentFromGovernmentServiceWithInvalidSearchResult() {
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn("Fetch Result");
        when(this.ifixDepartmentEntityConfig.getIfixMasterGovernmentSearchPath())
                .thenReturn("Ifix Master Government Search Path");
        when(this.ifixDepartmentEntityConfig.getIfixMasterGovernmentContextPath())
                .thenReturn("Ifix Master Government Context Path");
        when(this.ifixDepartmentEntityConfig.getIfixMasterGovernmentHost()).thenReturn("localhost");
        assertThrows(CustomException.class,
                () -> this.governmentUtil.getGovernmentFromGovernmentService("42", new RequestHeader()));
        verify(this.serviceRequestRepository).fetchResult((String) any(), (Object) any());
        verify(this.ifixDepartmentEntityConfig).getIfixMasterGovernmentContextPath();
        verify(this.ifixDepartmentEntityConfig).getIfixMasterGovernmentHost();
        verify(this.ifixDepartmentEntityConfig).getIfixMasterGovernmentSearchPath();
    }

    @Test
    void testGetGovernmentFromGovernmentServiceWithValidSearchResult() {
        Map<String, Object> response = objectMapper.convertValue(govSearchResNode, new TypeReference<Map<String, Object>>() {
        });
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn(response);
        when(this.ifixDepartmentEntityConfig.getIfixMasterGovernmentSearchPath())
                .thenReturn("Ifix Master Government Search Path");
        when(this.ifixDepartmentEntityConfig.getIfixMasterGovernmentContextPath())
                .thenReturn("Ifix Master Government Context Path");
        when(this.ifixDepartmentEntityConfig.getIfixMasterGovernmentHost()).thenReturn("localhost");
        List<String> governments = this.governmentUtil.getGovernmentFromGovernmentService("pb", new RequestHeader());
        assertNotNull(governments);
        assertTrue(!governments.isEmpty());

        verify(this.serviceRequestRepository).fetchResult((String) any(), (Object) any());
        verify(this.ifixDepartmentEntityConfig).getIfixMasterGovernmentContextPath();
        verify(this.ifixDepartmentEntityConfig).getIfixMasterGovernmentHost();
        verify(this.ifixDepartmentEntityConfig).getIfixMasterGovernmentSearchPath();
    }


    @Test
    void testGetGovernmentFromGovernmentServiceWithEmptyTenantId() {
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn("Fetch Result");
        when(this.ifixDepartmentEntityConfig.getIfixMasterGovernmentSearchPath())
                .thenReturn("Ifix Master Government Search Path");
        when(this.ifixDepartmentEntityConfig.getIfixMasterGovernmentContextPath())
                .thenReturn("Ifix Master Government Context Path");
        when(this.ifixDepartmentEntityConfig.getIfixMasterGovernmentHost()).thenReturn("localhost");
        assertTrue(this.governmentUtil.getGovernmentFromGovernmentService("", new RequestHeader()).isEmpty());
    }
}

