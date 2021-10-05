package org.egov.web.controllers;

import org.egov.config.TestDataFormatter;
import org.egov.service.FiscalEventService;
import org.egov.util.ResponseHeaderCreator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * API tests for FiscalApiController
 */

@AutoConfigureDataMongo
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@WebMvcTest(FiscalApiController.class)
public class FiscalApiControllerTest {

    @Autowired
    private TestDataFormatter testDataFormatter;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FiscalEventService fiscalEventService;

    @MockBean
    private ResponseHeaderCreator responseHeaderCreator;

    @BeforeAll
    void init() throws IOException {

    }

    @Test
    public void fiscalEventsV1PushPostSuccess() throws Exception {
        mockMvc.perform(post("/events/v1/_push").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isAccepted());
    }

    @Test
    public void fiscalEventsV1PushPostFailure() throws Exception {
        mockMvc.perform(post("/events/v1/_push").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void fiscalEventsV1SearchPostSuccess() throws Exception {
        mockMvc.perform(post("/events/v1/_search").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void fiscalEventsV1SearchPostFailure() throws Exception {
        mockMvc.perform(post("/events/v1/_search").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

}
