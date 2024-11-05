package org.egov.ifixmigrationtoolkit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.ifixmigrationtoolkit.models.MigrationRequest;
import org.egov.ifixmigrationtoolkit.service.DepartmentEntityMigrationService;
import org.egov.ifixmigrationtoolkit.service.DepartmentHierarchyLevelMigrationService;
import org.egov.ifixmigrationtoolkit.service.MasterDataMigrationService;
import org.egov.ifixmigrationtoolkit.service.FiscalEventMigrationService;
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
public class MigrationController {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private FiscalEventMigrationService fiscalEventMigrationService;

    @Autowired
    private DepartmentEntityMigrationService departmentEntityMigrationService;

    @Autowired
    private DepartmentHierarchyLevelMigrationService departmentHierarchyLevelMigrationService;

    @Autowired
    private MasterDataMigrationService masterDataMigrationService;

    @RequestMapping(value="/fiscal_event/v1/_migrate", method = RequestMethod.POST)
    public ResponseEntity<?> migrateDataToES(@RequestBody @Valid MigrationRequest request) throws JsonProcessingException {
        fiscalEventMigrationService.migrateData(request);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("SUCCESS", "Fiscal Event data migration job created successfully");
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    @RequestMapping(value="/department_entity/v1/_migrate", method = RequestMethod.POST)
    public ResponseEntity<?> migrateDepartmentEntityToPostgres(@RequestBody @Valid MigrationRequest request) throws JsonProcessingException {
        departmentEntityMigrationService.migrateDepartmentEntityData(request);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("SUCCESS", "Department entity data migration job created successfully");
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    @RequestMapping(value="/department_hierarchy/v1/_migrate", method = RequestMethod.POST)
    public ResponseEntity<?> migrateDepartmentHierarchyToPostgres(@RequestBody @Valid MigrationRequest request) throws JsonProcessingException {
        departmentHierarchyLevelMigrationService.migrateDepartmentHieraryLevelData(request);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("SUCCESS", "Department hierarchy level data migration job created successfully");
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
