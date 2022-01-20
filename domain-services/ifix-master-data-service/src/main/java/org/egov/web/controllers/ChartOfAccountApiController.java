package org.egov.web.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import org.egov.common.contract.response.ResponseHeader;
import org.egov.service.ChartOfAccountService;
import org.egov.util.ResponseHeaderCreator;
import org.egov.web.models.COARequest;
import org.egov.web.models.COAResponse;
import org.egov.web.models.COASearchRequest;
import org.egov.web.models.ChartOfAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2021-08-02T16:24:12.742+05:30")

@Controller
@RequestMapping("/chartOfAccount/v1")
public class ChartOfAccountApiController {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private ChartOfAccountService chartOfAccountService;

    @Autowired
    private ResponseHeaderCreator responseHeaderCreator;

    @Autowired
    public ChartOfAccountApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @RequestMapping(value = "/_create", method = RequestMethod.POST)
    @RolesAllowed("project-creator")
    public ResponseEntity<COAResponse> chartOfAccountV1CreatePost(@ApiParam(value = "Details for the new COA + RequestHeader (meta data of the API).", required = true) @Valid @RequestBody COARequest body) {

        COARequest coaRequest = chartOfAccountService.chartOfAccountV1CreatePost(body);
        ResponseHeader responseHeader = responseHeaderCreator.createResponseHeaderFromRequestHeader(body.getRequestHeader(), true);
        COAResponse coaResponse = COAResponse.builder().responseHeader(responseHeader)
                .chartOfAccounts(Collections.singletonList(coaRequest.getChartOfAccount())).build();

        return new ResponseEntity<COAResponse>(coaResponse, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/_search", method = RequestMethod.POST)
    public ResponseEntity<COAResponse> chartOfAccountV1SearchPost(@ApiParam(value = "RequestHeader meta data.", required = true) @Valid @RequestBody COASearchRequest body) {
        List<ChartOfAccount> chartOfAccounts = chartOfAccountService.chartOfAccountV1SearchPost(body);
        ResponseHeader responseHeader = responseHeaderCreator.createResponseHeaderFromRequestHeader(body.getRequestHeader(), true);
        COAResponse coaResponse = COAResponse.builder().responseHeader(responseHeader)
                .chartOfAccounts(chartOfAccounts).build();

        return new ResponseEntity<COAResponse>(coaResponse, HttpStatus.OK);
    }

}
