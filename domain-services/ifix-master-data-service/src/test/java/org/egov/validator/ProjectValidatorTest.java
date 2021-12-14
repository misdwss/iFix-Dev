package org.egov.validator;

import org.egov.config.TestDataFormatter;
import org.egov.tracer.model.CustomException;
import org.egov.util.DepartmentUtil;
import org.egov.util.ExpenditureUtil;
import org.egov.util.TenantUtil;
import org.egov.web.models.ProjectRequest;
import org.egov.web.models.ProjectSearchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
class ProjectValidatorTest {
    @Autowired
    private TestDataFormatter testDataFormatter;

    @Mock
    private DepartmentUtil departmentUtil;

    @Mock
    private ExpenditureUtil expenditureUtil;

    @InjectMocks
    private ProjectValidator projectValidator;

    @Mock
    private TenantUtil tenantUtil;

    private ProjectSearchRequest projectSearchRequest;
    private ProjectRequest projectCreateRequest;

    @BeforeEach
    public void setup() throws IOException {
        projectSearchRequest = testDataFormatter.getProjectSearchRequestData();
        projectCreateRequest = testDataFormatter.getProjectCreateRequestData();
    }

<<<<<<< HEAD
    @Test
    void testValidateProjectSearchRequestUserInfoException() {
        projectSearchRequest.getRequestHeader().setUserInfo(null);

        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectSearchRequest(projectSearchRequest),
                "User information is missing");
    }
=======
//    @Test
//    void testValidateProjectSearchRequestUserInfoException() {
//        projectSearchRequest.getRequestHeader().setUserInfo(null);
//
//        assertThrows(CustomException.class,
//                () -> projectValidator.validateProjectSearchRequest(projectSearchRequest),
//                "User information is missing");
//    }
>>>>>>> f070c61465b100be594b1916109e464860bcc3cb

    @Test
    void testValidateProjectSearchRequestTenantIdException() {
        projectSearchRequest.getCriteria().setTenantId(null);

        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectSearchRequest(projectSearchRequest),
                "Tenant id is missing in request data");
    }

    @Test
    void testValidateProjectSearchRequestTenantIdLengthException() {
        projectSearchRequest.getCriteria().setTenantId("0");

        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectSearchRequest(projectSearchRequest),
                "Tenant id length is invalid.");
    }

    @Test
    void testValidateProjectSearchRequestNameLengthException() {
        projectSearchRequest.getCriteria().setName("0");

        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectSearchRequest(projectSearchRequest),
                "Project name length is invalid.");
    }

    @Test
    void testValidateProjectSearchRequestExpenditureIdLengthException() {
        projectSearchRequest.getCriteria().setExpenditureId("0");

        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectSearchRequest(projectSearchRequest),
                "Expenditure id length is invalid.");
    }

    @Test
    void testValidateProjectSearchRequestDepartmentIdMinLengthException() {
        projectSearchRequest.getCriteria().setGetDepartmentEntitytId("0");

        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectSearchRequest(projectSearchRequest),
                "Department id length is invalid.");
    }

    @Test
    void testValidateProjectSearchRequestDepartmentIdMaxLengthException() {
        projectSearchRequest.getCriteria()
                .setGetDepartmentEntitytId("6413d1d9ef49fbc4b6dac524c5e92d487b010464abd89839e4e7591d100f41a6a");

        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectSearchRequest(projectSearchRequest),
                "Department id length is invalid.");
    }

    @Test
    void testValidateProjectSearchRequestLocationIdMinLengthException() {
        projectSearchRequest.getCriteria().setLocationId("0");

        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectSearchRequest(projectSearchRequest),
                "Location id length is invalid.");
    }

    @Test
    void testValidateProjectSearchRequestLocationIdMaxLengthException() {
        projectSearchRequest.getCriteria()
                .setLocationId("6413d1d9ef49fbc4b6dac524c5e92d487b010464abd89839e4e7591d100f41a6a");

        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectSearchRequest(projectSearchRequest),
                "Location id length is invalid.");
    }

    @Test
    void testValidateProjectCreateRequestPayloadException() {
        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectCreateRequest(null),
                "Request payload is missing some value");
    }

    @Test
    void testValidateProjectCreateRequestUserInfoException() {
        projectCreateRequest.getRequestHeader().setUserInfo(null);

        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectCreateRequest(projectCreateRequest),
                "User information is missing.");
    }

    @Test
    void testValidateProjectCreateRequestTenantIdException() {
        projectCreateRequest.getProject().setTenantId(null);

        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectCreateRequest(projectCreateRequest),
                "User information is missing");
    }

    @Test
    void testValidateProjectCreateRequestTenantIdLengthException() {
        projectCreateRequest.getProject().setTenantId("0");

        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectCreateRequest(projectCreateRequest),
                "Tenant id length is invalid.");
    }

    @Test
    void testValidateProjectCreateRequestCodeException() {
        projectCreateRequest.getProject().setCode(null);

        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectCreateRequest(projectCreateRequest),
                "Project code is missing in request data");
    }

    @Test
    void testValidateProjectCreateRequestNameException() {
        projectCreateRequest.getProject().setName(null);

        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectCreateRequest(projectCreateRequest),
                "Project name is missing in request data");
    }

    @Test
    void testValidateProjectCreateRequestExpenditureIdLengthException() {
        projectCreateRequest.getProject().setExpenditureId("0");

        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectCreateRequest(projectCreateRequest),
                "Expenditure id length is invalid. Length range [2-64]");
    }

    @Test
    void testValidateProjectCreateRequestDepartmentEntityIdLengthException() {
        projectCreateRequest.getProject().setDepartmentEntitytId("0");

        assertThrows(CustomException.class,
                () -> projectValidator.validateProjectCreateRequest(projectCreateRequest),
                "Department Entity id length is invalid.");
    }
}

