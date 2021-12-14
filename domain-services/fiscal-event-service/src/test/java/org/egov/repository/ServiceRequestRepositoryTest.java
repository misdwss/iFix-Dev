package org.egov.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tracer.model.ServiceCallException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.mock.http.client.MockAsyncClientHttpRequest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

<<<<<<< HEAD
import static org.junit.jupiter.api.Assertions.*;
=======
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
>>>>>>> f070c61465b100be594b1916109e464860bcc3cb
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class ServiceRequestRepositoryTest {


    @InjectMocks
    private ServiceRequestRepository serviceRequestRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RestTemplate restTemplate;

    @Test
    void testFetchResultNull() {
        ObjectMapper mapper = new ObjectMapper();
        assertNull((new ServiceRequestRepository(mapper, new RestTemplate())).fetchResult("Uri", "Request"));
    }

    @Test
    void testFetchResultWithObjectMapperObject() {
        when(objectMapper.configure((com.fasterxml.jackson.databind.SerializationFeature) any(), anyBoolean()))
                .thenReturn(new ObjectMapper());
        assertNull((new ServiceRequestRepository(objectMapper, new RestTemplate())).fetchResult("Uri", "Request"));
        verify(objectMapper).configure((com.fasterxml.jackson.databind.SerializationFeature) any(), anyBoolean());
    }

    @Test
    void testFetchResultWithNoClientResponse() throws IOException {
        when(objectMapper.configure((com.fasterxml.jackson.databind.SerializationFeature) any(), anyBoolean()))
                .thenReturn(new ObjectMapper());
        ClientHttpRequestFactory clientHttpRequestFactory = mock(ClientHttpRequestFactory.class);
        when(clientHttpRequestFactory.createRequest((java.net.URI) any(), (org.springframework.http.HttpMethod) any()))
                .thenReturn(new MockAsyncClientHttpRequest());
        assertNull((new ServiceRequestRepository(objectMapper, new RestTemplate(clientHttpRequestFactory)))
                .fetchResult("Uri", "Request"));
        verify(objectMapper).configure((com.fasterxml.jackson.databind.SerializationFeature) any(), anyBoolean());
        verify(clientHttpRequestFactory).createRequest((java.net.URI) any(), (org.springframework.http.HttpMethod) any());
    }

    @Test
    void testFetchResultWithHttpClientError() throws IOException {
        when(objectMapper.configure((com.fasterxml.jackson.databind.SerializationFeature) any(), anyBoolean()))
                .thenReturn(new ObjectMapper());
        ClientHttpRequestFactory clientHttpRequestFactory = mock(ClientHttpRequestFactory.class);
        when(clientHttpRequestFactory.createRequest((java.net.URI) any(), (org.springframework.http.HttpMethod) any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.CONTINUE));
        assertThrows(ServiceCallException.class,
                () -> (new ServiceRequestRepository(objectMapper, new RestTemplate(clientHttpRequestFactory)))
                        .fetchResult("Uri", "Request"));
        verify(objectMapper).configure((com.fasterxml.jackson.databind.SerializationFeature) any(), anyBoolean());
        verify(clientHttpRequestFactory).createRequest((java.net.URI) any(), (org.springframework.http.HttpMethod) any());
    }

    @Test
    void testFetchResultWithNullRestTemplate() {
        when(objectMapper.configure((com.fasterxml.jackson.databind.SerializationFeature) any(), anyBoolean()))
                .thenReturn(new ObjectMapper());
        assertNull((new ServiceRequestRepository(objectMapper, null)).fetchResult("Uri", "Request"));
        verify(objectMapper).configure((com.fasterxml.jackson.databind.SerializationFeature) any(), anyBoolean());
    }

    @Test
    void testFetchResult() throws IOException {
        when(objectMapper.configure((com.fasterxml.jackson.databind.SerializationFeature) any(), anyBoolean()))
                .thenReturn(new ObjectMapper());
<<<<<<< HEAD
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>("Success client response",HttpStatus.OK);
=======
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>("Success client response", HttpStatus.OK);
>>>>>>> f070c61465b100be594b1916109e464860bcc3cb

        doReturn(responseEntity).when(restTemplate).postForEntity((String) any(), (Object) any(), any());

        assertNull(serviceRequestRepository.fetchResult("Uri", "Request"));


    }
}

