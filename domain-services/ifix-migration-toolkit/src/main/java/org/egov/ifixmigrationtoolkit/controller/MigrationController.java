package org.egov.ifixmigrationtoolkit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.ifixmigrationtoolkit.models.MigrationRequest;
import org.egov.ifixmigrationtoolkit.service.FiscalEventMigrationService;
import org.egov.ifixmigrationtoolkit.service.MasterDataMigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/fiscal-event-pipeline")
public class MigrationController {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    FiscalEventMigrationService fiscalEventMigrationService;

    @Autowired
    MasterDataMigrationService masterDataMigrationService;

    @RequestMapping(value="/v1/_migrate", method = RequestMethod.POST)
    public ResponseEntity<?> migrateDataToES(@RequestBody @Valid MigrationRequest request) throws JsonProcessingException {
        Map<String, Object> responseMap = fiscalEventMigrationService.migrateData(request);
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    @RequestMapping(value="/coa/v1/_migrate", method = RequestMethod.POST)
    public ResponseEntity<?> migrateMasterDataToPostgres(@RequestBody @Valid MigrationRequest request) throws JsonProcessingException {
        masterDataMigrationService.migrateMasterData(request);
        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("SUCCESS", "Migration job for migrating coa data to postgres is successfully created.");
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

}
