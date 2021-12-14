package org.egov.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.egov.common.contract.request.RequestHeader;
import org.egov.config.FiscalEventPostProcessorConfig;
import org.egov.config.TestDataFormatter;
import org.egov.resposioty.ServiceRequestRepository;
import org.egov.tracer.model.CustomException;
import org.egov.web.models.DepartmentEntity;
import org.egov.web.models.FiscalEvent;
import org.egov.web.models.FiscalEventRequest;
import org.egov.web.models.Project;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class ProjectUtilTest {

    @Mock
    private FiscalEventPostProcessorConfig fiscalEventPostProcessorConfig;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProjectUtil projectUtil;

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private TestDataFormatter testDataFormatter;

    private Project project;
    private FiscalEventRequest fiscalEventRequest;
    private JsonNode projectJsonNode;
    private JsonNode deptEntityJsonNode;

    @BeforeAll
    void init() throws IOException {
        projectJsonNode = testDataFormatter.getProjectSearchResponse();
        deptEntityJsonNode = testDataFormatter.getDeptEntitySearchResponse();

        project = objectMapper.convertValue(projectJsonNode.get("project").get(0), Project.class);

        fiscalEventRequest = testDataFormatter.getFiscalEventValidatedData();
    }

    @Test
    void testGetProjectReferenceWithDefaultFiscalEventRequest() {
        ObjectNode objectNode = new ObjectNode(new JsonNodeFactory(true));
        when(this.objectMapper.createObjectNode()).thenReturn(objectNode);
        assertSame(objectNode, this.projectUtil.getProjectReference(new FiscalEventRequest()));
        verify(this.objectMapper).createObjectNode();
    }

    @Test
    void testGetProjectReferenceWithNullFiscalEventRequest() {
        ObjectNode objectNode = new ObjectNode(new JsonNodeFactory(true));
        when(this.objectMapper.createObjectNode()).thenReturn(objectNode);
        assertSame(objectNode, this.projectUtil.getProjectReference(null));
        verify(this.objectMapper).createObjectNode();
    }

    @Test
    void testGetProjectReferenceWithDefaultFiscalEventData() {
        ObjectNode objectNode = new ObjectNode(new JsonNodeFactory(true));
        when(this.objectMapper.createObjectNode()).thenReturn(objectNode);
        RequestHeader requestHeader = new RequestHeader();
        assertSame(objectNode,
                this.projectUtil.getProjectReference(new FiscalEventRequest(requestHeader, new FiscalEvent())));
        verify(this.objectMapper).createObjectNode();
    }

    @Test
    void testGetProjectReferenceWithInvalidProjectId() {
        ObjectNode objectNode = new ObjectNode(new JsonNodeFactory(true));
        when(this.objectMapper.createObjectNode()).thenReturn(objectNode);
        FiscalEventRequest fiscalEventRequest = mock(FiscalEventRequest.class);
        when(fiscalEventRequest.getFiscalEvent()).thenReturn(new FiscalEvent());
        when(fiscalEventRequest.getRequestHeader()).thenReturn(new RequestHeader());
        assertSame(objectNode, this.projectUtil.getProjectReference(fiscalEventRequest));
        verify(this.objectMapper).createObjectNode();
        verify(fiscalEventRequest, atLeast(1)).getFiscalEvent();
        verify(fiscalEventRequest).getRequestHeader();
    }

    @Test
    void testGetProjectReferenceWithNullFiscalEvent() {
        ObjectNode objectNode = new ObjectNode(new JsonNodeFactory(true));
        when(this.objectMapper.createObjectNode()).thenReturn(objectNode);
        FiscalEventRequest fiscalEventRequest = mock(FiscalEventRequest.class);
        when(fiscalEventRequest.getFiscalEvent()).thenReturn(null);
        when(fiscalEventRequest.getRequestHeader()).thenReturn(new RequestHeader());
        assertSame(objectNode, this.projectUtil.getProjectReference(fiscalEventRequest));
        verify(this.objectMapper).createObjectNode();
        verify(fiscalEventRequest).getFiscalEvent();
        verify(fiscalEventRequest).getRequestHeader();
    }

    @Test
    void testGetProjectReferenceWithValidResponseFromService() throws IllegalArgumentException {
        Map<String, Object> response = objectMapper.convertValue(projectJsonNode, new TypeReference<Map<String, Object>>() {
        });
        when(this.serviceRequestRepository.fetchResult((String) any(), (Object) any())).thenReturn(response);
        when(this.objectMapper.createObjectNode()).thenReturn(new ObjectNode(new JsonNodeFactory(true)));
        when(this.fiscalEventPostProcessorConfig.getIfixMasterProjectSearchPath())
                .thenReturn("Ifix Master Project Search Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterProjectContextPath())
                .thenReturn("Ifix Master Project Context Path");
        when(this.fiscalEventPostProcessorConfig.getIfixMasterProjectHost()).thenReturn("localhost");

        assertNotNull(this.projectUtil.getProjectReference(fiscalEventRequest));

        verify(this.serviceRequestRepository).fetchResult((String) any(), (Object) any());
        verify(this.objectMapper).convertValue((Object) any(), (Class<Object>) any());
        verify(this.fiscalEventPostProcessorConfig).getIfixMasterProjectContextPath();
        verify(this.fiscalEventPostProcessorConfig).getIfixMasterProjectHost();
        verify(this.fiscalEventPostProcessorConfig).getIfixMasterProjectSearchPath();
    }

    @Test
    void testGetDepartmentEntityFromProjectWithInvalidJsonNode() throws JsonProcessingException {
        when(this.objectMapper.treeToValue((com.fasterxml.jackson.core.TreeNode) any(), (Class<Object>) any()))
                .thenThrow(mock(JsonProcessingException.class));
        assertThrows(CustomException.class,
                () -> this.projectUtil.getDepartmentEntityFromProject(DoubleNode.valueOf(10.0)));
        verify(this.objectMapper).treeToValue((com.fasterxml.jackson.core.TreeNode) any(), (Class<Object>) any());
    }

    @Test
    void testGetDepartmentEntityFromProject() throws JsonProcessingException {
        DepartmentEntity departmentEntity = objectMapper.convertValue(deptEntityJsonNode,DepartmentEntity.class);
        when(this.objectMapper.treeToValue((com.fasterxml.jackson.core.TreeNode) any(), (Class<Object>) any()))
                .thenReturn(departmentEntity);
        DepartmentEntity actualResult = projectUtil.getDepartmentEntityFromProject(deptEntityJsonNode);
        assertNotNull(actualResult);
        assertNotNull(actualResult.getId());
        assertNotNull(actualResult.getAncestry());
        assertFalse(actualResult.getAncestry().isEmpty());
    }
}

