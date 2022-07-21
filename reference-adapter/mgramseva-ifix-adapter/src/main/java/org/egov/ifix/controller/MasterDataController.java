package org.egov.ifix.controller;

import lombok.extern.slf4j.Slf4j;
import org.egov.ifix.models.CoaMappingDTO;
import org.egov.ifix.repository.BillingServiceRepository;
import org.egov.ifix.repository.FiscalEventRepository;
import org.egov.ifix.repository.MdmsRepository;
import org.egov.ifix.repository.MgramsevaChallanRepository;
import org.egov.ifix.service.ChartOfAccountService;
import org.egov.ifix.service.PspclEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/master/v1")
public class MasterDataController {

    @Autowired
    FiscalEventRepository fiscalEventRepository;
    @Autowired
    MdmsRepository mdmsRepository;
    @Autowired
    private ChartOfAccountService chartOfAccountService;
    @Autowired
    private MgramsevaChallanRepository mgramSevaChallanRepository;
    @Autowired
    private PspclEventService pspclEventService;

    @Autowired
    private BillingServiceRepository billingServiceRepository;

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
        } else {
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
    }
}
