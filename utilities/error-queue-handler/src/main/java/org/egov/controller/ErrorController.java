package org.egov.controller;

import org.egov.model.ErrorDetail;
import org.egov.service.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/error/")
public class ErrorController {

    @Autowired
    private ErrorService errorService;

    @GetMapping("/all")
    public ResponseEntity<List<ErrorDetail>> getAllErrors() {
        return new ResponseEntity(errorService.getAllErrorDetails(), HttpStatus.OK);
    }
}
