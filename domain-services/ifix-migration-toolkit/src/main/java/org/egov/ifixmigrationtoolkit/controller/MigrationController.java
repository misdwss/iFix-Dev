package org.egov.ifixmigrationtoolkit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.ifixmigrationtoolkit.models.FiscalEvent;
import org.egov.ifixmigrationtoolkit.models.MigrationRequest;
import org.egov.ifixmigrationtoolkit.service.MigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/fiscal-event-pipeline")
public class MigrationController {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    MigrationService migrationService;

    @RequestMapping(value="/v1/_migrate", method = RequestMethod.POST)
    public ResponseEntity<FiscalEvent> migrateDataToES(@RequestBody @Valid MigrationRequest request) throws JsonProcessingException {
        migrationService.migrateData(request);
        return new ResponseEntity<>(new FiscalEvent(), HttpStatus.OK);
    }

}
