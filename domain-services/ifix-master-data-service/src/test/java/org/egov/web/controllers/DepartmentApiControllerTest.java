package org.egov.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * API tests for DepartmentApiController
 */
//@Ignore
//@RunWith(SpringRunner.class)
//@WebMvcTest(DepartmentApiController.class)
//@Import(TestConfiguration.class)
public class DepartmentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void departmentV1CreatePostSuccess() throws Exception {
        mockMvc.perform(post("/eGovTrial/iFIX-Master-Data/1.0.0/department/v1/_create").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void departmentV1CreatePostFailure() throws Exception {
        mockMvc.perform(post("/eGovTrial/iFIX-Master-Data/1.0.0/department/v1/_create").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void departmentV1SearchPostSuccess() throws Exception {
        mockMvc.perform(post("/eGovTrial/iFIX-Master-Data/1.0.0/department/v1/_search").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void departmentV1SearchPostFailure() throws Exception {
        mockMvc.perform(post("/eGovTrial/iFIX-Master-Data/1.0.0/department/v1/_search").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

}
