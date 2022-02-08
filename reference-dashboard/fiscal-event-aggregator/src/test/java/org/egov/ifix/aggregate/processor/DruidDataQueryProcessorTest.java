package org.egov.ifix.aggregate.processor;

import in.zapr.druid.druidry.client.DruidClient;
import in.zapr.druid.druidry.client.exception.QueryException;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.egov.ifix.aggregate.config.AbstractIT;
import org.egov.ifix.aggregate.config.ConfigProperties;
import org.egov.ifix.aggregate.config.ContainersEnvironment;
import org.egov.ifix.aggregate.config.PostgresTestContainer;
import org.egov.ifix.aggregate.util.FiscalEventAggregateUtil;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureEmbeddedDatabase
class DruidDataQueryProcessorTest/* extends ContainersEnvironment*/ {

//    @ClassRule
//    public static PostgreSQLContainer postgreSQLContainer = PostgresTestContainer.getInstance();

    @Mock
    private ConfigProperties configProperties;

    @Mock
    private DruidClient druidClient;

    @InjectMocks
    private DruidDataQueryProcessor druidDataQueryProcessor;

    @Mock
    private FiscalEventAggregateUtil fiscalEventAggregateUtil;

    @Test
    void testFetchFiscalEventFromDruid() {
        when(this.fiscalEventAggregateUtil.getFiscalYear()).thenReturn(new HashMap<>(1));
        assertTrue(this.druidDataQueryProcessor.fetchFiscalEventFromDruid().isEmpty());
        verify(this.fiscalEventAggregateUtil, atLeast(1)).getFiscalYear();
    }

    @Test
    void testFetchFiscalEventFromDruidWithEmptyResult() throws QueryException {
        HashMap<String, Integer> stringIntegerMap = new HashMap<>(1);
        stringIntegerMap.put("CURRENT_FISCAL_YEAR", 2022);
        when(this.fiscalEventAggregateUtil.getFiscalYear()).thenReturn(stringIntegerMap);
        when(this.druidClient.query((in.zapr.druid.druidry.query.DruidQuery) any(), (Class<Object>) any()))
                .thenReturn(new ArrayList<>());
        when(this.configProperties.getFiscalEventDataSource()).thenReturn("Fiscal Event Data Source");
        assertTrue(this.druidDataQueryProcessor.fetchFiscalEventFromDruid().isEmpty());
        verify(this.fiscalEventAggregateUtil).getFiscalYear();
        verify(this.druidClient, atLeast(1)).query((in.zapr.druid.druidry.query.DruidQuery) any(), (Class<Object>) any());
        verify(this.configProperties, atLeast(1)).getFiscalEventDataSource();
    }

    @Test
    void testFetchFiscalEventFromDruidWithErrorInResult() throws QueryException {
        HashMap<String, Integer> stringIntegerMap = new HashMap<>(2);
        stringIntegerMap.put("CURRENT_FISCAL_YEAR", 2022);
        stringIntegerMap.put("PREVIOUS_FISCAL_YEAR", 2021);
        when(this.fiscalEventAggregateUtil.getFiscalYear()).thenReturn(stringIntegerMap);
        when(this.druidClient.query((in.zapr.druid.druidry.query.DruidQuery) any(), (Class<Object>) any()))
                .thenThrow(new QueryException("An error occurred"));
        when(this.configProperties.getFiscalEventDataSource()).thenReturn("Fiscal Event Data Source");
        assertTrue(this.druidDataQueryProcessor.fetchFiscalEventFromDruid().isEmpty());
        verify(this.druidClient, atLeast(1)).query((in.zapr.druid.druidry.query.DruidQuery) any(), (Class<Object>) any());
        verify(this.configProperties, atLeast(1)).getFiscalEventDataSource();
    }
}

