package org.egov.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestHeader;
import org.egov.config.FiscalEventPostProcessorConfig;
import org.egov.config.TestDataFormatter;
import org.egov.resposioty.ServiceRequestRepository;
import org.egov.tracer.model.CustomException;
import org.egov.models.Department;
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
class DepartmentUtilTest {

    @InjectMocks
    private DepartmentUtil departmentUtil;

    @Mock
    private FiscalEventPostProcessorConfig fiscalEventPostProcessorConfig;

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private TestDataFormatter testDataFormatter;

    private Department department;
    private FiscalEventRequest fiscalEventRequest;
    private JsonNode deptJsonNode;

    @BeforeAll
    void init() throws IOException {
        deptJsonNode = testDataFormatter.getDepartmentSearchResponse();
        department = objectMapper.convertValue(deptJsonNode.get("department").get(0), Department.class);

        fiscalEventRequest = testDataFormatter.getFiscalEventValidatedData();
    }

    @Test
    void testGetDepartmentReferenceWithInvalidServiceResponse() {
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn("Fetch Result");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterDepartmentSearchPath())
                .thenReturn("Ifix Master Department Search Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterDepartmentContextPath())
                .thenReturn("Ifix Master Department Context Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterDepartmentHost()).thenReturn("localhost");
        RequestHeader requestHeader = new RequestHeader();
        assertThrows(CustomException.class,
                () -> this.departmentUtil.getDepartmentReference("pb",
                        "3d9ef18a-361a-40cf-b142-dd6f998e1ac1", requestHeader));
        verify(this.serviceRequestRepository).fetchResult((String) any(), (Object) any());
        verify(this.fiscalEventPostProcessorConfig).getIfixMasterDepartmentContextPath();
        verify(this.fiscalEventPostProcessorConfig).getIfixMasterDepartmentHost();
        verify(this.fiscalEventPostProcessorConfig).getIfixMasterDepartmentSearchPath();
    }

    @Test
    void testGetDepartmentReferenceWithEmptyTenantId() {
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn("Fetch Result");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterDepartmentSearchPath())
                .thenReturn("Ifix Master Department Search Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterDepartmentContextPath())
                .thenReturn("Ifix Master Department Context Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterDepartmentHost()).thenReturn("localhost");
        assertTrue(this.departmentUtil.getDepartmentReference("", "42", new RequestHeader()).isEmpty());
    }

    @Test
    void testGetDepartmentReferenceWithInvalidDepartmentId() {
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn("Fetch Result");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterDepartmentSearchPath())
                .thenReturn("Ifix Master Department Search Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterDepartmentContextPath())
                .thenReturn("Ifix Master Department Context Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterDepartmentHost()).thenReturn("localhost");
        assertTrue(this.departmentUtil.getDepartmentReference("42", "", new RequestHeader()).isEmpty());
    }

    @Test
    void testGetDepartmentReferenceWithValidResponseFromServiceRepo() {
        Map<String, Object> response = objectMapper.convertValue(deptJsonNode, new TypeReference<Map<String, Object>>() {
        });
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn(response);
        when(this.fiscalEventPostProcessorConfig.getIfixMasterDepartmentSearchPath())
                .thenReturn("Ifix Master Department Search Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterDepartmentContextPath())
                .thenReturn("Ifix Master Department Context Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterDepartmentHost()).thenReturn("localhost");
        List<Department> departmentList = departmentUtil.getDepartmentReference("pb",
                "3d9ef18a-361a-40cf-b142-dd6f998e1ac1", fiscalEventRequest.getRequestHeader());

        assertNotNull(departmentList);
        assertFalse(departmentList.isEmpty());
    }
}

