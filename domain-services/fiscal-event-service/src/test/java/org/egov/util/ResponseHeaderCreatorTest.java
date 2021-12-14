package org.egov.util;

<<<<<<< HEAD
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

=======
>>>>>>> f070c61465b100be594b1916109e464860bcc3cb
import org.egov.common.contract.request.RequestHeader;
import org.egov.common.contract.request.UserInfo;
import org.egov.common.contract.response.ResponseHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

=======
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

>>>>>>> f070c61465b100be594b1916109e464860bcc3cb
@ContextConfiguration(classes = {ResponseHeaderCreator.class})
@ExtendWith(SpringExtension.class)
class ResponseHeaderCreatorTest {

    @InjectMocks
    private ResponseHeaderCreator responseHeaderCreator;

    @Test
    void testCreateResponseHeaderFromRequestHeaderWithBlankValuesSuccess() {
        ResponseHeader result = this.responseHeaderCreator
                .createResponseHeaderFromRequestHeader(new RequestHeader(), true);
        assertNull(result.getCorrelationId());
        assertNull(result.getVersion());
        assertNull(result.getTs());
        assertEquals("successful", result.getStatus());
        assertEquals("", result.getSignature());
        assertNull(result.getMsgId());
    }

    @Test
    void testCreateResponseHeaderFromRequestHeaderWithBlankValuesFail() {
        ResponseHeader result = this.responseHeaderCreator
                .createResponseHeaderFromRequestHeader(new RequestHeader(), false);
        assertNull(result.getCorrelationId());
        assertNull(result.getVersion());
        assertNull(result.getTs());
        assertEquals("failed", result.getStatus());
        assertEquals("", result.getSignature());
        assertNull(result.getMsgId());
    }


    @Test
    void testCreateResponseHeaderFromRequestHeader() {
        ResponseHeader result = this.responseHeaderCreator
                .createResponseHeaderFromRequestHeader(new RequestHeader(1L, "1.0.2", "42", new UserInfo(), "42", "Signature"),
                        true);
        assertEquals("42", result.getCorrelationId());
        assertEquals("1.0.2", result.getVersion());
        assertEquals(1L, result.getTs().longValue());
        assertEquals("successful", result.getStatus());
        assertEquals("Signature", result.getSignature());
        assertEquals("42", result.getMsgId());
    }

}

