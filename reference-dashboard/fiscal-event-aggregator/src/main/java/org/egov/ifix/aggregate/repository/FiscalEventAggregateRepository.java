package org.egov.ifix.aggregate.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.ifix.aggregate.model.FiscalEventAggregate;
import org.egov.ifix.aggregate.repository.mapper.FiscalEventAggregateQuery;
import org.egov.ifix.aggregate.repository.mapper.FiscalEventAggregatedDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class FiscalEventAggregateRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public int[] upsert(List<FiscalEventAggregate> fiscalEventAggregates){
        return (jdbcTemplate.batchUpdate(FiscalEventAggregateQuery.UPSERT_QUERY_FOR_FISCAL_EVENT_AGGREGATE
                , new FiscalEventAggregatedDataMapper(fiscalEventAggregates)));
    }
}
