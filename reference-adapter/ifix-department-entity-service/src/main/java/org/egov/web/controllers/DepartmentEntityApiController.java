package org.egov.web.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import org.egov.common.contract.response.ResponseHeader;
import org.egov.service.DepartmentEntityService;
import org.egov.util.ResponseHeaderCreator;
import org.egov.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2021-08-23T11:51:49.710+05:30")

@Controller
@RequestMapping("/departmentEntity/v1")
public class DepartmentEntityApiController {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    DepartmentEntityService departmentEntityService;

    @Autowired
    private ResponseHeaderCreator responseHeaderCreator;

    @Autowired
    public DepartmentEntityApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    /**
     * @param body
     * @return
     */
    @RequestMapping(value = "/_create", method = RequestMethod.POST)
    public ResponseEntity<DepartmentEntityResponse> departmentEntityV1CreatePost(
            @ApiParam(value = "Details for the new Department Entity RequestHeader (meta data of the API).",
                    required = true)
            @Valid @RequestBody DepartmentEntityRequest body) {

        DepartmentEntityRequest departmentEntityRequest = departmentEntityService.createDepartmentEntity(body);

        ResponseHeader responseHeader = responseHeaderCreator
                .createResponseHeaderFromRequestHeader(body.getRequestHeader(), true);

        DepartmentEntityResponse departmentEntityResponse = DepartmentEntityResponse.builder()
                .responseHeader(responseHeader)
                .departmentEntity(Collections.singletonList(departmentEntityRequest.getDepartmentEntity())).build();

        return new ResponseEntity<DepartmentEntityResponse>(departmentEntityResponse, HttpStatus.OK);
    }

    /**
     * @param body
     * @return
     */
    @RequestMapping(value = "/_search", method = RequestMethod.POST)
    public ResponseEntity<DepartmentEntityResponse> departmentEntityV1SearchPost(
            @ApiParam(value = "RequestHeader meta data.", required = true)
            @Valid @RequestBody DepartmentEntitySearchRequest body) {
        List<? extends DepartmentEntityAbstract> departmentEntityList = departmentEntityService.findAllByCriteria(body);
        ResponseHeader responseHeader = responseHeaderCreator
                .createResponseHeaderFromRequestHeader(body.getRequestHeader(), true);
        DepartmentEntityResponse departmentEntityResponse = DepartmentEntityResponse.builder()
                .responseHeader(responseHeader)
                .departmentEntity(departmentEntityList).build();

        return new ResponseEntity<DepartmentEntityResponse>(departmentEntityResponse, HttpStatus.OK);
    }

    /**
     * @param body
     * @return
     */
    @RequestMapping(value = "/_update", method = RequestMethod.POST)
    public ResponseEntity<DepartmentEntityResponse> departmentEntityV1UpdatePost(
            @ApiParam(value = "Details for the update Department Entity RequestHeader (meta data of the API).",
                    required = true)
            @Valid @RequestBody DepartmentEntityRequest body) {

        DepartmentEntityRequest departmentEntityRequest = departmentEntityService.updateDepartmentEntity(body);

        ResponseHeader responseHeader = responseHeaderCreator
                .createResponseHeaderFromRequestHeader(body.getRequestHeader(), true);

        DepartmentEntityResponse departmentEntityResponse = DepartmentEntityResponse.builder()
                .responseHeader(responseHeader)
                .departmentEntity(Collections.singletonList(departmentEntityRequest.getDepartmentEntity())).build();

        return new ResponseEntity<DepartmentEntityResponse>(departmentEntityResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/_count", method = RequestMethod.POST)
    public ResponseEntity<DepartmentEntityCountResponse> departmentEntityV1Count(
            @Valid @RequestBody DepartmentEntityPlainSearchRequest body) {

        Long count = departmentEntityService.getDepartmentEntityCount(body);
        ResponseHeader responseHeader = responseHeaderCreator
                .createResponseHeaderFromRequestHeader(body.getRequestHeader(), true);

        DepartmentEntityCountResponse departmentEntityCountResponse = DepartmentEntityCountResponse.builder()
                .responseHeader(responseHeader)
                .count(count).build();

        return new ResponseEntity<DepartmentEntityCountResponse>(departmentEntityCountResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/_plainsearch", method = RequestMethod.POST)
    public ResponseEntity<DepartmentEntityResponse> departmentEntityV1PlainSearchPost(
            @Valid @RequestBody DepartmentEntityPlainSearchRequest body) {

        List<? extends DepartmentEntityAbstract> departmentEntityList = departmentEntityService
                .departmentEntityPlainSearchPost(body);

        ResponseHeader responseHeader = responseHeaderCreator
                .createResponseHeaderFromRequestHeader(body.getRequestHeader(), true);

        DepartmentEntityResponse departmentEntityResponse = DepartmentEntityResponse.builder()
                .responseHeader(responseHeader)
                .departmentEntity(departmentEntityList).build();

        return new ResponseEntity<DepartmentEntityResponse>(departmentEntityResponse, HttpStatus.OK);
    }

}
