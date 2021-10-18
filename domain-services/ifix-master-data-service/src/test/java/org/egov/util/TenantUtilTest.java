package org.egov.util;

import org.egov.common.contract.request.RequestHeader;
import org.egov.config.MasterDataServiceConfiguration;
import org.egov.config.TestDataFormatter;
import org.egov.repository.ServiceRequestRepository;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class TenantUtilTest {

    @Autowired
    private TestDataFormatter testDataFormatter;

    @Mock
    private MasterDataServiceConfiguration configuration;

    @Mock
    private ServiceRequestRepository searchRequestRepository;

    @InjectMocks
    private TenantUtil tenantUtil;

    private Object governmentResponse;
    private RequestHeader requestHeader = new RequestHeader();

    @BeforeAll
    public void init() throws IOException {
        governmentResponse = testDataFormatter.getGovernmentSearchResponseDataAsObject();

        doReturn(new String()).when(configuration).getIfixMasterGovernmentHost();
        doReturn(new String()).when(configuration).getIfixMasterGovernmentContextPath();
        doReturn(new String()).when(configuration).getIfixMasterGovernmentSearchPath();
    }


    @Test
    void testValidateTenant() {
        doReturn(governmentResponse).when(searchRequestRepository).fetchResult((String) any(), any());

        assertTrue(tenantUtil.validateTenant("pb", requestHeader));
    }

    @Test
    void testFalseValidateTenant() {
        assertFalse(tenantUtil.validateTenant(null, requestHeader));
    }

    @Test
    void testValidateTenantException() {
        doReturn(new Object()).when(searchRequestRepository).fetchResult((String) any(), any());

        assertThrows(CustomException.class,
                () -> tenantUtil.validateTenant("pb", requestHeader));
    }
}