package org.egov.fiscaleventaggregatorservice.processor;


import in.zapr.druid.druidry.aggregator.DoubleSumAggregator;
import in.zapr.druid.druidry.aggregator.DruidAggregator;
import in.zapr.druid.druidry.aggregator.LongSumAggregator;
import in.zapr.druid.druidry.client.DruidClient;
import in.zapr.druid.druidry.client.exception.QueryException;
import in.zapr.druid.druidry.dataSource.TableDataSource;
import in.zapr.druid.druidry.dimension.DruidDimension;
import in.zapr.druid.druidry.dimension.SimpleDimension;
import in.zapr.druid.druidry.granularity.Granularity;
import in.zapr.druid.druidry.granularity.PredefinedGranularity;
import in.zapr.druid.druidry.granularity.SimpleGranularity;
import in.zapr.druid.druidry.postAggregator.ArithmeticFunction;
import in.zapr.druid.druidry.postAggregator.ArithmeticPostAggregator;
import in.zapr.druid.druidry.postAggregator.DruidPostAggregator;
import in.zapr.druid.druidry.postAggregator.FieldAccessPostAggregator;
import in.zapr.druid.druidry.query.aggregation.DruidTopNQuery;
import in.zapr.druid.druidry.query.config.Interval;
import in.zapr.druid.druidry.topNMetric.SimpleMetric;
import in.zapr.druid.druidry.topNMetric.TopNMetric;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class DruidDataQueryProcessor {

    @Autowired
    private DruidClient druidClient;


    //TODO
    public void fetchFiscalEventFromDruid() {
        System.out.println("*********** Druid_client : " + druidClient);

        TableDataSource dataSource =  new TableDataSource("fiscal-event");
//        SelectorFilter selectorFilter1 = new SelectorFilter("dim1", "some_value");
//        SelectorFilter selectorFilter2 = new SelectorFilter("dim2", "some_other_val");
//
//        AndFilter filter = new AndFilter(Arrays.asList(selectorFilter1, selectorFilter2));

        DruidAggregator aggregator1 = new LongSumAggregator("count", "count");
        DruidAggregator aggregator2 = new DoubleSumAggregator("some_metric", "some_metric");

        FieldAccessPostAggregator fieldAccessPostAggregator1
                = new FieldAccessPostAggregator("some_metric", "some_metric");

        FieldAccessPostAggregator fieldAccessPostAggregator2
                = new FieldAccessPostAggregator("count", "count");

        DruidPostAggregator postAggregator = ArithmeticPostAggregator.builder()
                .name("sample_divide")
                .function(ArithmeticFunction.DIVIDE)
                .fields(Arrays.asList(fieldAccessPostAggregator1, fieldAccessPostAggregator2))
                .build();

        DateTime startTime = new DateTime(2013, 8, 31, 0, 0, 0, DateTimeZone.UTC);
        DateTime endTime = new DateTime(2013, 9, 3, 0, 0, 0, DateTimeZone.UTC);
        Interval interval = new Interval(startTime, endTime);

        Granularity granularity = new SimpleGranularity(PredefinedGranularity.ALL);
        DruidDimension dimension = new SimpleDimension("sample_dim");
        TopNMetric metric = new SimpleMetric("count");

        DruidTopNQuery query = DruidTopNQuery.builder()
                .dataSource(dataSource)
                .dimension(dimension)
                .threshold(5)
                .topNMetric(metric)
                .granularity(granularity)
                .filter(null)
                .aggregators(Arrays.asList(aggregator1, aggregator2))
                .postAggregators(Collections.singletonList(postAggregator))
                .intervals(Collections.singletonList(interval))
                .build();

        try {
            List<Object> responses = druidClient.query(query, Object.class);
        } catch (QueryException e) {
            e.printStackTrace();
        }


    }
}
