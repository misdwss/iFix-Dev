package org.egov.repository;


import java.util.ArrayList;
import java.util.List;

import org.egov.repository.querybuilder.FiscalEventQueryBuilder;
import org.egov.repository.rowmapper.FiscalEventRowMapper;
import org.egov.web.models.Criteria;
import org.egov.web.models.FiscalEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class FiscalEventRepository {

	/*
	 * @Autowired private MongoTemplate mongoTemplate;
	 */

    @Autowired
    private FiscalEventQueryBuilder eventQueryBuilder;

    @Autowired
    private FiscalEventRowMapper fiscalEventRowMapper;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public List<FiscalEvent> searchFiscalEvent(Criteria searchCriteria) {
    	List<FiscalEvent> fiscalEvents = new ArrayList<>();
    	List<Object> preparedStmtList = new ArrayList<>();

    	try {
    		String fiscalEventSearchQuery = eventQueryBuilder.buildSearchQuery(searchCriteria, preparedStmtList);
    		log.info("Fiscal event search query: " + fiscalEventSearchQuery);
    		fiscalEvents = jdbcTemplate.query(fiscalEventSearchQuery, preparedStmtList.toArray(), fiscalEventRowMapper);
    	} catch(Exception e) {

    		// DOUBT - EXCEPTION SHOULD BE THROWN INSTEAD OF RETURNING EMPTY LIST IN CASE OF ERROR ??

    		log.error("Exception while fetching data from DB: " + e);
			return fiscalEvents;
    	}

    	return fiscalEvents;
    }
}
