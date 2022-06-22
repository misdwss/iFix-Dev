package org.egov.ifix.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.egov.ifix.models.CoaMappingDTO;
import org.egov.ifix.models.fiscalEvent.FiscalEventResponseDTO;
import org.egov.ifix.models.mdms.Tenant;
import org.egov.ifix.models.mgramseva.CreateChallanResponseDTO;
import org.egov.ifix.models.mgramseva.SearchChallanResponseDTO;
import org.egov.ifix.repository.BillingServiceRepository;
import org.egov.ifix.repository.FiscalEventRepository;
import org.egov.ifix.repository.MdmsRepository;
import org.egov.ifix.repository.MgramsevaChallanRepository;
import org.egov.ifix.service.ChartOfAccountService;
import org.egov.ifix.service.PspclEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/master/v1")
public class MasterDataController {

    @Autowired
    private ChartOfAccountService chartOfAccountService;

    @Autowired
    private MgramsevaChallanRepository mgramSevaChallanRepository;

    @Autowired
    FiscalEventRepository fiscalEventRepository;

    @Autowired
    MdmsRepository mdmsRepository;

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


    @GetMapping("/demo/start_challan")
    public ResponseEntity<String> startChallan() {
//        billingServiceRepository.fetchBillFromBillingService("pb.lodhipur", "EB-2022-23-1186");

//        pspclEventService.pushPspclEventToMgramseva("Demand");
        pspclEventService.pushPspclEventToMgramseva("Receipt");

        return new ResponseEntity<>("Done", HttpStatus.ACCEPTED);
    }
}
