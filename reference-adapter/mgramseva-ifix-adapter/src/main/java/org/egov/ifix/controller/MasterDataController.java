package org.egov.ifix.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.egov.ifix.models.CoaMappingDTO;
import org.egov.ifix.models.Event;
import org.egov.ifix.models.EventRequest;
import org.egov.ifix.models.EventResponse;
import org.egov.ifix.service.ChartOfAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/master/v1")
public  class MasterDataController {

    @Autowired
    ChartOfAccountService chartOfAccountService;

    /**
     * @param coaMappingDTO
     * @return
     */
    @PostMapping("/mapping/coa/search")
    public ResponseEntity<CoaMappingDTO> coaMappingSearch(@RequestBody CoaMappingDTO coaMappingDTO) {

       Optional<CoaMappingDTO> coaMappingDTOOptional = chartOfAccountService
               .getMappedCoaIdByClientCoaCode(coaMappingDTO);

        if (coaMappingDTOOptional.isPresent()) {
            return new ResponseEntity<CoaMappingDTO>(coaMappingDTOOptional.get(), HttpStatus.ACCEPTED);
        }else {
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
    }

}
