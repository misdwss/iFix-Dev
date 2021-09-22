package org.egov.ifix.aggregate.processor;


import com.fasterxml.jackson.databind.JsonNode;
import in.zapr.druid.druidry.aggregator.CountAggregator;
import in.zapr.druid.druidry.aggregator.DoubleSumAggregator;
import in.zapr.druid.druidry.aggregator.DruidAggregator;
import in.zapr.druid.druidry.client.DruidClient;
import in.zapr.druid.druidry.client.exception.QueryException;
import in.zapr.druid.druidry.dataSource.TableDataSource;
import in.zapr.druid.druidry.dimension.DefaultDimension;
import in.zapr.druid.druidry.dimension.DruidDimension;
import in.zapr.druid.druidry.dimension.enums.OutputType;
import in.zapr.druid.druidry.granularity.Granularity;
import in.zapr.druid.druidry.granularity.PredefinedGranularity;
import in.zapr.druid.druidry.granularity.SimpleGranularity;
import in.zapr.druid.druidry.query.DruidQuery;
import in.zapr.druid.druidry.query.aggregation.DruidGroupByQuery;
import in.zapr.druid.druidry.query.config.Interval;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.ifix.aggregate.config.ConfigProperties;
import org.egov.ifix.aggregate.model.FiscalEventAggregate;
import org.egov.ifix.aggregate.repository.FiscalEventAggregateRepository;
import org.egov.ifix.aggregate.util.FiscalEventAggregateConstants;
import org.egov.ifix.aggregate.util.FiscalEventAggregateUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class DruidDataQueryProcessor {

    @Autowired
    private DruidClient druidClient;

    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    private FiscalEventAggregateUtil aggregateUtil;

    @Autowired
    private FiscalEventAggregateRepository aggregateRepository;

    /**
     * Fetch the fiscal event data with total sum amount , count from druid date store
     * based on the group of project id, coa id, event type
     */
    public void fetchFiscalEventFromDruid() {
        DruidQuery groupByQuery = getDruidQueryForGroupbyProjectIdAndCoaIdAndEventType();
        DruidQuery distinctProjectQuery = getDruidQueryForProjectDetails();
        DruidQuery distinctCoaIdQuery = getDruidQueryForCoaDetails();

        List<Object> groupByResponses = null;
        List<Object> distinctProjectResponses = null;
        List<Object> distinctCoaIdResponses = null;
        try {
            groupByResponses = druidClient.query(groupByQuery, Object.class);
            distinctProjectResponses = druidClient.query(distinctProjectQuery, Object.class);
            distinctCoaIdResponses = druidClient.query(distinctCoaIdQuery, Object.class);
        } catch (QueryException e) {
            log.error("Exception occurred while quering the data from druid data store : {}", e.getDruidError());
        }
        log.debug("Group by Response : {}", groupByResponses);
        if (groupByResponses == null || groupByResponses.isEmpty()) {
            log.info("There are no fiscal event data with group by of project id, event type and coa id");
            return;
        }

        //Create a map of key as project id and event node details as value
        Map<String, JsonNode> projectNodeMap = aggregateUtil.getProjectDetailsMap(distinctProjectResponses);
        //Create a map of key as coa id and event node details as value
        Map<String, JsonNode> coaNodeMap = aggregateUtil.getCOADetailsMap(distinctCoaIdResponses);
        //Get the details by project id and coa id map key and create a final List<FiscalEventAggregate>
        List<FiscalEventAggregate> fiscalEventAggregateList = aggregateUtil.getFiscalEventAggregateData(groupByResponses
                , projectNodeMap, coaNodeMap
                , configProperties.getFiscalPeriod() != null ? configProperties.getFiscalPeriod() : FiscalEventAggregateConstants.DEFAULT_FISCAL_PERIOD);

        //pass the list for upsert
        if (fiscalEventAggregateList != null && !fiscalEventAggregateList.isEmpty()) {
            int[] upsertedRecord = aggregateRepository.upsert(fiscalEventAggregateList);
            log.debug("Record -> {} upserted successfully", upsertedRecord);
        }
    }


    private DruidQuery getDruidQueryForCoaDetails() {
        TableDataSource dataSource = new TableDataSource(configProperties.getFiscalEventDataSource());

        Map<String, Integer> intervalYearMap = aggregateUtil.getIntervalYearMap();

        DateTime startTime = new DateTime(intervalYearMap.get(FiscalEventAggregateConstants.START_YEAR), 04, 1, 0, 0, 0, DateTimeZone.UTC);
        DateTime endTime = new DateTime(intervalYearMap.get(FiscalEventAggregateConstants.END_YEAR), 03, 31, 0, 0, 0, DateTimeZone.UTC);
        Interval interval = new Interval(startTime, endTime);

        Granularity granularity = new SimpleGranularity(PredefinedGranularity.ALL);

        List<DruidDimension> druidDimensions = new ArrayList<>();
        druidDimensions.add(new DefaultDimension("coa.id", "coa.id", OutputType.STRING));

        druidDimensions.add(new DefaultDimension("coa.coaCode", "coa.coaCode", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("coa.groupHead", "coa.groupHead", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("coa.groupHeadName", "coa.groupHeadName", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("coa.majorHead", "coa.majorHead", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("coa.majorHeadName", "coa.majorHeadName", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("coa.minorHead", "coa.minorHead", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("coa.minorHeadName", "coa.minorHeadName", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("coa.objectHead", "coa.objectHead", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("coa.objectHeadName", "coa.objectHeadName", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("coa.subHead", "coa.subHead", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("coa.subHeadName", "coa.subHeadName", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("coa.subMajorHead", "coa.subMajorHead", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("coa.subMajorHeadName", "coa.subMajorHeadName", OutputType.STRING));

//        List<DruidAggregator> aggregators = new ArrayList<>();
//        aggregators.add(new DistinctCountAggregator("coa.id","coa.id"));

        return (DruidGroupByQuery.builder()
                .dataSource(dataSource)
                .dimensions(druidDimensions)
                .granularity(granularity)
                .filter(null)
                //.aggregators(aggregators)
                .intervals(Collections.singletonList(interval))
                .build());
    }

    private DruidQuery getDruidQueryForProjectDetails() {
        TableDataSource dataSource = new TableDataSource(configProperties.getFiscalEventDataSource());
        Map<String, Integer> intervalYearMap = aggregateUtil.getIntervalYearMap();

        DateTime startTime = new DateTime(intervalYearMap.get(FiscalEventAggregateConstants.START_YEAR), 04, 1, 0, 0, 0, DateTimeZone.UTC);
        DateTime endTime = new DateTime(intervalYearMap.get(FiscalEventAggregateConstants.END_YEAR), 03, 31, 0, 0, 0, DateTimeZone.UTC);
        Interval interval = new Interval(startTime, endTime);

        Granularity granularity = new SimpleGranularity(PredefinedGranularity.ALL);

        List<DruidDimension> druidDimensions = new ArrayList<>();
        druidDimensions.add(new DefaultDimension("project.id", "project.id", OutputType.STRING));

        druidDimensions.add(new DefaultDimension("department.code", "department.code", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("department.id", "department.id", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("department.name", "department.name", OutputType.STRING));

        druidDimensions.add(new DefaultDimension("expenditure.code", "expenditure.code", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("expenditure.id", "expenditure.id", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("expenditure.name", "expenditure.name", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("expenditure.type", "expenditure.type", OutputType.STRING));

        druidDimensions.add(new DefaultDimension("tenantId", "tenantId", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("government.id", "government.id", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("government.name", "government.name", OutputType.STRING));

        druidDimensions.add(new DefaultDimension("departmentEntity.id", "departmentEntity.id", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("departmentEntity.code", "departmentEntity.code", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("departmentEntity.name", "departmentEntity.name", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("departmentEntity.hierarchyLevel", "departmentEntity.hierarchyLevel", OutputType.STRING));

        druidDimensions.add(new DefaultDimension("project.code", "project.code", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("project.name", "project.name", OutputType.STRING));

//        druidDimensions.add(new DefaultDimension("id", "id", OutputType.STRING));
//        druidDimensions.add(new DefaultDimension("ingestionTime", "ingestionTime", OutputType.LONG));
//        druidDimensions.add(new DefaultDimension("payment", "payment", OutputType.DOUBLE));
//        druidDimensions.add(new DefaultDimension("referenceId", "referenceId", OutputType.STRING));
//        druidDimensions.add(new DefaultDimension("receipt", "receipt", OutputType.DOUBLE));
//        druidDimensions.add(new DefaultDimension("eventId", "eventId", OutputType.STRING));
//        druidDimensions.add(new DefaultDimension("eventType", "eventType", OutputType.STRING));
//        druidDimensions.add(new DefaultDimension("fromBillingPeriod", "fromBillingPeriod", OutputType.LONG));
//        druidDimensions.add(new DefaultDimension("toBillingPeriod", "toBillingPeriod", OutputType.LONG));
//        druidDimensions.add(new DefaultDimension("bill", "bill", OutputType.DOUBLE));
//        druidDimensions.add(new DefaultDimension("demand", "demand", OutputType.DOUBLE));
//        druidDimensions.add(new DefaultDimension("version", "version", OutputType.STRING));

        int hierarchyLevel = FiscalEventAggregateConstants.DEFAULT_HIERARCHY_LEVEL;
        if (StringUtils.isNotBlank(configProperties.getDepartmentHierarchyLevel())) {
            hierarchyLevel = Integer.parseInt(configProperties.getDepartmentHierarchyLevel());
        }

        for (int i = 0; i <= hierarchyLevel; i++) {
            druidDimensions.add(new DefaultDimension("departmentEntity.ancestry[" + i + "].code", "departmentEntity.ancestry[" + i + "].code", OutputType.STRING));
            druidDimensions.add(new DefaultDimension("departmentEntity.ancestry[" + i + "].hierarchyLevel", "departmentEntity.ancestry[" + i + "].hierarchyLevel", OutputType.STRING));
            druidDimensions.add(new DefaultDimension("departmentEntity.ancestry[" + i + "].id", "departmentEntity.ancestry[" + i + "].id", OutputType.STRING));
            druidDimensions.add(new DefaultDimension("departmentEntity.ancestry[" + i + "].name", "departmentEntity.ancestry[" + i + "].name", OutputType.STRING));
        }

//        List<DruidAggregator> aggregators = new ArrayList<>();
//        aggregators.add(new DistinctCountAggregator("project.id","project.id"));
//        aggregators.add(new DoubleSumAggregator("amount", "amount"));

        return (DruidGroupByQuery.builder()
                .dataSource(dataSource)
                .dimensions(druidDimensions)
                .granularity(granularity)
                .filter(null)
                //.aggregators(aggregators)
                .intervals(Collections.singletonList(interval))
                .build());
    }

    private DruidGroupByQuery getDruidQueryForGroupbyProjectIdAndCoaIdAndEventType() {
        TableDataSource dataSource = new TableDataSource(configProperties.getFiscalEventDataSource());
        Map<String, Integer> intervalYearMap = aggregateUtil.getIntervalYearMap();

        DateTime startTime = new DateTime(intervalYearMap.get(FiscalEventAggregateConstants.START_YEAR), 04, 1, 0, 0, 0, DateTimeZone.UTC);
        DateTime endTime = new DateTime(intervalYearMap.get(FiscalEventAggregateConstants.END_YEAR), 03, 31, 0, 0, 0, DateTimeZone.UTC);
        Interval interval = new Interval(startTime, endTime);

        Granularity granularity = new SimpleGranularity(PredefinedGranularity.ALL);

        List<DruidDimension> druidDimensions = new ArrayList<>();
        druidDimensions.add(new DefaultDimension("project.id", "project.id", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("coa.id", "coa.id", OutputType.STRING));
        druidDimensions.add(new DefaultDimension("eventType", "eventType", OutputType.STRING));

        List<DruidAggregator> aggregators = new ArrayList<>();
        aggregators.add(new CountAggregator("Count"));
        aggregators.add(new DoubleSumAggregator("amount", "amount"));

        return (DruidGroupByQuery.builder()
                .dataSource(dataSource)
                .dimensions(druidDimensions)
                .granularity(granularity)
                .filter(null)
                .aggregators(aggregators)
                .intervals(Collections.singletonList(interval))
                .build());
    }
}
