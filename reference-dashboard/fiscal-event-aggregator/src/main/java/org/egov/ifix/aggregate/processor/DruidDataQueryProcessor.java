package org.egov.ifix.aggregate.processor;


import in.zapr.druid.druidry.aggregator.CountAggregator;
import in.zapr.druid.druidry.aggregator.DoubleSumAggregator;
import in.zapr.druid.druidry.aggregator.DruidAggregator;
import in.zapr.druid.druidry.aggregator.LongSumAggregator;
import in.zapr.druid.druidry.client.DruidClient;
import in.zapr.druid.druidry.client.exception.QueryException;
import in.zapr.druid.druidry.dataSource.TableDataSource;
import in.zapr.druid.druidry.dimension.DefaultDimension;
import in.zapr.druid.druidry.dimension.DimensionSpec;
import in.zapr.druid.druidry.dimension.DruidDimension;
import in.zapr.druid.druidry.dimension.SimpleDimension;
import in.zapr.druid.druidry.dimension.enums.OutputType;
import in.zapr.druid.druidry.granularity.Granularity;
import in.zapr.druid.druidry.granularity.PredefinedGranularity;
import in.zapr.druid.druidry.granularity.SimpleGranularity;
import in.zapr.druid.druidry.postAggregator.ArithmeticFunction;
import in.zapr.druid.druidry.postAggregator.ArithmeticPostAggregator;
import in.zapr.druid.druidry.postAggregator.DruidPostAggregator;
import in.zapr.druid.druidry.postAggregator.FieldAccessPostAggregator;
import in.zapr.druid.druidry.query.DruidQuery;
import in.zapr.druid.druidry.query.aggregation.DruidGroupByQuery;
import in.zapr.druid.druidry.query.aggregation.DruidTopNQuery;
import in.zapr.druid.druidry.query.config.Interval;
import in.zapr.druid.druidry.topNMetric.SimpleMetric;
import in.zapr.druid.druidry.topNMetric.TopNMetric;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        DateTime startTime = new DateTime(2021, 1, 1, 0, 0, 0, DateTimeZone.UTC);
        DateTime endTime = new DateTime(2021, 12, 31, 0, 0, 0, DateTimeZone.UTC);
        Interval interval = new Interval(startTime, endTime);
        Granularity granularity = new SimpleGranularity(PredefinedGranularity.ALL);

        DruidDimension dimension1 = new DefaultDimension("project.id","project.id", OutputType.STRING);
        DruidDimension dimension2 = new DefaultDimension("coa.id","coa.id", OutputType.STRING);
        DruidDimension dimension3 = new DefaultDimension("eventType","eventType", OutputType.STRING);
        List<DruidDimension> druidDimensions =  new ArrayList<>();
        druidDimensions.add(dimension1);
        druidDimensions.add(dimension2);
        druidDimensions.add(dimension3);
//        SelectorFilter selectorFilter1 = new SelectorFilter("dim1", "some_value");
//        SelectorFilter selectorFilter2 = new SelectorFilter("dim2", "some_other_val");
//
//        AndFilter filter = new AndFilter(Arrays.asList(selectorFilter1, selectorFilter2));

        DruidAggregator aggregator1 = new CountAggregator("Count");
        DruidAggregator aggregator2 = new DoubleSumAggregator("amount", "amount");
        List<DruidAggregator> aggregators =  new ArrayList<>();
        aggregators.add(aggregator1);
        aggregators.add(aggregator2);

//        FieldAccessPostAggregator fieldAccessPostAggregator1
//                = new FieldAccessPostAggregator("some_metric", "some_metric");
//
//        FieldAccessPostAggregator fieldAccessPostAggregator2
//                = new FieldAccessPostAggregator("count", "count");

//        DruidPostAggregator postAggregator = ArithmeticPostAggregator.builder()
//                .name("sample_divide")
//                .function(ArithmeticFunction.DIVIDE)
//                .fields(Arrays.asList(fieldAccessPostAggregator1, fieldAccessPostAggregator2))
//                .build();





        TopNMetric metric = new SimpleMetric("count");

        DruidQuery query = DruidGroupByQuery.builder()
                .dataSource(dataSource)
                .dimensions(druidDimensions)
                //.threshold(5)
               // .topNMetric(metric)
                .granularity(granularity)
                .filter(null)
                .aggregators(aggregators)
               // .postAggregators(Collections.singletonList(postAggregator))
                .intervals(Collections.singletonList(interval))
                .build();
//        String responses = null;
//        try {
//             responses = druidClient.query(query);
//        } catch (QueryException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("Response : "+responses);

    }
}
