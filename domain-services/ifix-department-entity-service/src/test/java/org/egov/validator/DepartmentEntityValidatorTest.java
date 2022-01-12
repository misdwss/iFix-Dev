package org.egov.validator;

import org.egov.common.contract.request.RequestHeader;
import org.egov.config.TestDataFormatter;
import org.egov.repository.DepartmentEntityRepository;
import org.egov.tracer.model.CustomException;
import org.egov.util.DepartmentHierarchyUtil;
import org.egov.util.GovernmentUtil;
import org.egov.web.models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class DepartmentEntityValidatorTest {
    @MockBean
    private DepartmentEntityRepository departmentEntityRepository;

    @Autowired
    private DepartmentEntityValidator departmentEntityValidator;

    @MockBean
    private DepartmentHierarchyUtil departmentHierarchyUtil;

    @MockBean
    private GovernmentUtil governmentUtil;

    @Autowired
    private TestDataFormatter testDataFormatter;

    private DepartmentEntityRequest departmentEntityRequest;
    private DepartmentEntity departmentEntity;
    private DepartmentEntitySearchRequest departmentEntitySearchRequest;
    private DepartmentEntityResponse searchDepartmentEntityResponse;
    private DepartmentEntityResponse createDepartmentEntityResponse;

    @BeforeAll
    void init() throws IOException {
        departmentEntityRequest = testDataFormatter.getDeptEntityCreateRequestData();
        departmentEntity = departmentEntityRequest.getDepartmentEntity();
        departmentEntitySearchRequest = testDataFormatter.getDeptEntitySearchRequestData();
        searchDepartmentEntityResponse = testDataFormatter.getDeptEntitySearchResponseData();
        createDepartmentEntityResponse = testDataFormatter.getDeptEntityCreateResponseData();
    }

    @Test
    void testValidateDepartmentEntityRequestWithNullRequest() {
        assertThrows(CustomException.class,
                () -> this.departmentEntityValidator.validateDepartmentEntityRequest(new DepartmentEntityRequest()));
        assertThrows(CustomException.class, () -> this.departmentEntityValidator.validateDepartmentEntityRequest(null));
    }

    @Test
    void testValidateDepartmentEntityRequestWithDefaultRequest() {
        RequestHeader requestHeader = new RequestHeader();
        assertThrows(CustomException.class, () -> this.departmentEntityValidator
                .validateDepartmentEntityRequest(new DepartmentEntityRequest(requestHeader, new DepartmentEntity())));
    }

    @Test
    void testValidateDepartmentEntityRequestWithNullUserInfo() {
        DepartmentEntityRequest departmentEntityRequest = mock(DepartmentEntityRequest.class);
        when(departmentEntityRequest.getDepartmentEntity()).thenReturn(new DepartmentEntity());
        when(departmentEntityRequest.getRequestHeader()).thenReturn(new RequestHeader());
        assertThrows(CustomException.class,
                () -> this.departmentEntityValidator.validateDepartmentEntityRequest(departmentEntityRequest));
        verify(departmentEntityRequest, atLeast(1)).getDepartmentEntity();
        verify(departmentEntityRequest, atLeast(1)).getRequestHeader();
    }

    @Test
    void testValidateDepartmentEntityRequestWithNullDepartmentEntity() {
        DepartmentEntityRequest departmentEntityRequest = mock(DepartmentEntityRequest.class);
        when(departmentEntityRequest.getDepartmentEntity()).thenReturn(null);
        when(departmentEntityRequest.getRequestHeader()).thenReturn(new RequestHeader());
        assertThrows(CustomException.class,
                () -> this.departmentEntityValidator.validateDepartmentEntityRequest(departmentEntityRequest));
        verify(departmentEntityRequest).getDepartmentEntity();
        verify(departmentEntityRequest).getRequestHeader();
    }

    @Test
    void testValidateDepartmentEntityRequestWithEmptyDepartmentEntities() {
        DepartmentEntityRequest departmentEntityRequest = mock(DepartmentEntityRequest.class);
        when(departmentEntityRequest.getDepartmentEntity()).thenReturn(new DepartmentEntity(new ArrayList<String>()));
        when(departmentEntityRequest.getRequestHeader()).thenReturn(new RequestHeader());
        assertThrows(CustomException.class,
                () -> this.departmentEntityValidator.validateDepartmentEntityRequest(departmentEntityRequest));
        verify(departmentEntityRequest, atLeast(1)).getDepartmentEntity();
        verify(departmentEntityRequest, atLeast(1)).getRequestHeader();
    }

    @Test
    void testValidateDepartmentEntityRequestWithEmptyDepartmentChildrenList() {
        DepartmentEntity departmentEntity = mock(DepartmentEntity.class);
        when(departmentEntity.getChildren()).thenReturn(new ArrayList<String>());
        DepartmentEntityRequest departmentEntityRequest = mock(DepartmentEntityRequest.class);
        when(departmentEntityRequest.getDepartmentEntity()).thenReturn(departmentEntity);
        when(departmentEntityRequest.getRequestHeader()).thenReturn(new RequestHeader());
        assertThrows(CustomException.class,
                () -> this.departmentEntityValidator.validateDepartmentEntityRequest(departmentEntityRequest));
        verify(departmentEntityRequest, atLeast(1)).getDepartmentEntity();
        verify(departmentEntityRequest, atLeast(1)).getRequestHeader();
        verify(departmentEntity, atLeast(1)).getChildren();
    }

    @Test
    void testValidateDepartmentEntityRequestWithInvalidChildrenList() {
        when(this.departmentEntityRepository.searchChildDepartment((List<String>) any(), (Integer) any()))
                .thenReturn(new ArrayList<DepartmentEntity>());

        ArrayList<String> stringList = new ArrayList<String>();
        stringList.add("7e3a29b4-8c26-4562-a882-2d0be1d2406a");
        DepartmentEntity departmentEntity = mock(DepartmentEntity.class);
        when(departmentEntity.getChildren()).thenReturn(stringList);
        DepartmentEntityRequest departmentEntityRequest = mock(DepartmentEntityRequest.class);
        when(departmentEntityRequest.getDepartmentEntity()).thenReturn(departmentEntity);
        when(departmentEntityRequest.getRequestHeader()).thenReturn(new RequestHeader());
        assertThrows(CustomException.class,
                () -> this.departmentEntityValidator.validateDepartmentEntityRequest(departmentEntityRequest));
        verify(this.departmentEntityRepository).searchChildDepartment((List<String>) any(), (Integer) any());
        verify(departmentEntityRequest, atLeast(1)).getDepartmentEntity();
        verify(departmentEntityRequest, atLeast(1)).getRequestHeader();
        verify(departmentEntity, atLeast(1)).getChildren();
    }

    @Test
    void testValidateDepartmentEntityRequestWithChildrenSizeMismatch() {
        ArrayList<DepartmentEntity> departmentEntityList = new ArrayList<DepartmentEntity>();
        departmentEntityList.add(new DepartmentEntity());
        departmentEntityList.add(new DepartmentEntity());
        when(this.departmentEntityRepository.searchChildDepartment((List<String>) any(), (Integer) any()))
                .thenReturn(departmentEntityList);

        ArrayList<String> stringList = new ArrayList<String>();
        stringList.add("7e3a29b4-8c26-4562-a882-2d0be1d2406a");
        DepartmentEntity departmentEntity = mock(DepartmentEntity.class);
        when(departmentEntity.getChildren()).thenReturn(stringList);
        DepartmentEntityRequest departmentEntityRequest = mock(DepartmentEntityRequest.class);
        when(departmentEntityRequest.getDepartmentEntity()).thenReturn(departmentEntity);
        when(departmentEntityRequest.getRequestHeader()).thenReturn(new RequestHeader());
        assertThrows(CustomException.class,
                () -> this.departmentEntityValidator.validateDepartmentEntityRequest(departmentEntityRequest));
        verify(this.departmentEntityRepository).searchChildDepartment((List<String>) any(), (Integer) any());
        verify(departmentEntityRequest, atLeast(1)).getDepartmentEntity();
        verify(departmentEntityRequest, atLeast(1)).getRequestHeader();
        verify(departmentEntity, atLeast(1)).getChildren();
    }

    @Test
    void testValidateDepartmentEntityRequestWithMissingMandatoryFields() {
        doReturn(createDepartmentEntityResponse.getDepartmentEntity()).when(this.departmentEntityRepository)
                .searchChildDepartment((List<String>) any(), (Integer) any());
        DepartmentEntity departmentEntity = mock(DepartmentEntity.class);
        when(departmentEntity.getChildren()).thenReturn(new ArrayList<String>());
        DepartmentEntityRequest departmentEntityRequest1 = mock(DepartmentEntityRequest.class);
        when(departmentEntityRequest1.getDepartmentEntity()).thenReturn(departmentEntity);
        when(departmentEntityRequest1.getRequestHeader())
                .thenReturn(departmentEntityRequest.getRequestHeader());
        assertThrows(CustomException.class,
                () -> this.departmentEntityValidator.validateDepartmentEntityRequest(departmentEntityRequest1));
        verify(departmentEntityRequest1, atLeast(1)).getDepartmentEntity();
        verify(departmentEntityRequest1, atLeast(1)).getRequestHeader();
        verify(departmentEntity, atLeast(1)).getChildren();
    }

    @Test
    void testValidateDepartmentEntityRequestWithInvalidHierarchyLevel() {
        doReturn(createDepartmentEntityResponse.getDepartmentEntity()).when(this.departmentEntityRepository)
                .searchChildDepartment((List<String>) any(), (Integer) any());
        assertThrows(CustomException.class,
                () -> this.departmentEntityValidator.validateDepartmentEntityRequest(departmentEntityRequest));
    }

    @Test
    void testValidateDepartmentEntityRequestWithInvalidTenantId() {
        doReturn(new ArrayList<DepartmentHierarchyLevel>()).when(departmentHierarchyUtil)
                .validateHierarchyLevelMetaData((String) any(), (Integer) any(), (String) any());
        doReturn(createDepartmentEntityResponse.getDepartmentEntity()).when(this.departmentEntityRepository)
                .searchChildDepartment((List<String>) any(), (Integer) any());
        assertThrows(CustomException.class,
                () -> this.departmentEntityValidator.validateDepartmentEntityRequest(departmentEntityRequest));
    }

    @Test
    void testValidateDepartmentEntityRequestWithEmptyHierarchyLevels() {
        List<String> governments = new ArrayList<>();
        governments.add("pb");
        doReturn(governments).when(governmentUtil)
                .getGovernmentFromGovernmentService((String) any(), any());
        doReturn(new ArrayList<DepartmentHierarchyLevel>()).when(departmentHierarchyUtil)
                .validateHierarchyLevelMetaData((String) any(), (Integer) any(), (String) any());
        doReturn(createDepartmentEntityResponse.getDepartmentEntity()).when(this.departmentEntityRepository)
                .searchChildDepartment((List<String>) any(), (Integer) any());
        assertThrows(CustomException.class,
                () -> this.departmentEntityValidator.validateDepartmentEntityRequest(departmentEntityRequest));
    }

    @Test
    void testValidateDepartmentEntityRequestWithInvalidTenantIdLength() {
        departmentEntityRequest.getDepartmentEntity().setTenantId("p");
        doReturn(createDepartmentEntityResponse.getDepartmentEntity()).when(this.departmentEntityRepository)
                .searchChildDepartment((List<String>) any(), (Integer) any());
        assertThrows(CustomException.class,
                () -> this.departmentEntityValidator.validateDepartmentEntityRequest(departmentEntityRequest));
    }

    @Test
    void testValidateDepartmentEntityRequestWithInvalidDeptNameLength() {
        departmentEntityRequest.getDepartmentEntity().setName("");
        List<String> governments = new ArrayList<>();
        governments.add("pb");
        doReturn(governments).when(governmentUtil)
                .getGovernmentFromGovernmentService((String) any(), any());
        doReturn(createDepartmentEntityResponse.getDepartmentEntity()).when(this.departmentEntityRepository)
                .searchChildDepartment((List<String>) any(), (Integer) any());
        assertThrows(CustomException.class,
                () -> this.departmentEntityValidator.validateDepartmentEntityRequest(departmentEntityRequest));
    }

    @Test
    void testValidateDepartmentEntityRequestWithInvalidDeptCodeLength() {
        departmentEntityRequest.getDepartmentEntity().setCode("");
        List<String> governments = new ArrayList<>();
        governments.add("pb");
        doReturn(governments).when(governmentUtil)
                .getGovernmentFromGovernmentService((String) any(), any());
        doReturn(createDepartmentEntityResponse.getDepartmentEntity()).when(this.departmentEntityRepository)
                .searchChildDepartment((List<String>) any(), (Integer) any());
        assertThrows(CustomException.class,
                () -> this.departmentEntityValidator.validateDepartmentEntityRequest(departmentEntityRequest));
    }
}

