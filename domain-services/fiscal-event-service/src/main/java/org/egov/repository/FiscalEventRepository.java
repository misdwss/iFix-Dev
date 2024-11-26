package org.egov.repository;


import lombok.extern.slf4j.Slf4j;
import org.egov.repository.querybuilder.FiscalEventQueryBuilder;
import org.egov.web.models.Criteria;
import org.egov.web.models.PlainsearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class FiscalEventRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FiscalEventQueryBuilder eventQueryBuilder;

    public List<Object> searchFiscalEvent(Criteria searchCriteria) {
        Query searchQuery = eventQueryBuilder.buildSearchQuery(searchCriteria);
        return (mongoTemplate.find(searchQuery, Object.class, "fiscal_event"));
    }

    public List<Object> plainSearchFiscalEvent(PlainsearchCriteria searchCriteria) {
        Query searchQuery = eventQueryBuilder.buildPlainSearchQuery(searchCriteria);
        return (mongoTemplate.find(searchQuery, Object.class, "fiscal_event"));
    }

    public long getFiscalEventsCount(PlainsearchCriteria criteria) {
        criteria.setIsCountCall(Boolean.TRUE);
        Query countQuery = eventQueryBuilder.buildPlainSearchQuery(criteria);
        return (mongoTemplate.count(countQuery, Integer.class, "fiscal_event"));
    }
}
