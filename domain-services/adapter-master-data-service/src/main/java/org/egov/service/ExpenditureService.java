package org.egov.service;

import org.egov.repository.ExpenditureRepository;
import org.egov.web.models.Expenditure;
import org.egov.web.models.ExpenditureRequest;
import org.egov.web.models.ExpenditureSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenditureService {

    @Autowired
    ExpenditureRepository expenditureRepository;

    @Autowired
    private ExpenditureEnrichmentService enricher;

    /**
     * @param expenditureSearchRequest
     * @return
     */
    public List<Expenditure> findAllByCriteria(ExpenditureSearchRequest expenditureSearchRequest) {
        return expenditureRepository.findAllByCriteria(expenditureSearchRequest.getCriteria());
    }

    public ExpenditureRequest createV1Expenditure(ExpenditureRequest expenditureRequest) {
        enricher.enrichCreateExpenditure(expenditureRequest);
        expenditureRepository.save(expenditureRequest.getExpenditure());
        return expenditureRequest;
    }
}
