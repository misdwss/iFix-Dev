package org.egov.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class FiscalEventPostProcessorConfigTest {
    @Test
    void testInitialize() {
        FiscalEventPostProcessorConfig fiscalEventPostProcessorConfig = new FiscalEventPostProcessorConfig("UTC",
                "Fiscal Event Mongo Db Sink", "Fiscal Event Dereference Topic", "Fiscal Event Flattened Topic", "1234",
                "localhost", "Ifix Master Coa Context Path", "Ifix Master Coa Search Path", "localhost",
                "Ifix Master Government Context Path", "Ifix Master Government Search Path", "localhost",
                "Ifix Master Project Context Path", "Ifix Master Project Search Path", "localhost",
                "Ifix Master Expenditure Context Path", "Ifix Master Expenditure Search Path", "localhost",
                "Ifix Master Department Context Path", "Ifix Master Department Search Path");
        fiscalEventPostProcessorConfig.initialize();
        assertEquals("Fiscal Event Dereference Topic", fiscalEventPostProcessorConfig.getFiscalEventDereferenceTopic());
        assertEquals("UTC", fiscalEventPostProcessorConfig.getTimeZone());
        assertEquals("Ifix Master Project Search Path", fiscalEventPostProcessorConfig.getIfixMasterProjectSearchPath());
        assertEquals("localhost", fiscalEventPostProcessorConfig.getIfixMasterProjectHost());
        assertEquals("Ifix Master Project Context Path", fiscalEventPostProcessorConfig.getIfixMasterProjectContextPath());
        assertEquals("Ifix Master Government Search Path",
                fiscalEventPostProcessorConfig.getIfixMasterGovernmentSearchPath());
        assertEquals("localhost", fiscalEventPostProcessorConfig.getIfixMasterGovernmentHost());
        assertEquals("Ifix Master Government Context Path",
                fiscalEventPostProcessorConfig.getIfixMasterGovernmentContextPath());
        assertEquals("Ifix Master Expenditure Search Path",
                fiscalEventPostProcessorConfig.getIfixMasterExpenditureSearchPath());
        assertEquals("localhost", fiscalEventPostProcessorConfig.getIfixMasterExpenditureHost());
        assertEquals("Ifix Master Expenditure Context Path",
                fiscalEventPostProcessorConfig.getIfixMasterExpenditureContextPath());
        assertEquals("Ifix Master Department Search Path",
                fiscalEventPostProcessorConfig.getIfixMasterDepartmentSearchPath());
        assertEquals("localhost", fiscalEventPostProcessorConfig.getIfixMasterDepartmentHost());
        assertEquals("Ifix Master Department Context Path",
                fiscalEventPostProcessorConfig.getIfixMasterDepartmentContextPath());
        assertEquals("Ifix Master Coa Search Path", fiscalEventPostProcessorConfig.getIfixMasterCoaSearchPath());
        assertEquals("localhost", fiscalEventPostProcessorConfig.getIfixMasterCoaHost());
        assertEquals("Ifix Master Coa Context Path", fiscalEventPostProcessorConfig.getIfixMasterCoaContextPath());
        assertEquals("Fiscal Event Mongo Db Sink", fiscalEventPostProcessorConfig.getFiscalEventMongoDbSink());
        assertEquals("Fiscal Event Flattened Topic", fiscalEventPostProcessorConfig.getFiscalEventFlattenedTopic());
        assertEquals("1234", fiscalEventPostProcessorConfig.getFiscalEventDruidTopic());
    }

    @Test
    void testJacksonConverter() {
        FiscalEventPostProcessorConfig fiscalEventPostProcessorConfig = new FiscalEventPostProcessorConfig("UTC",
                "Fiscal Event Mongo Db Sink", "Fiscal Event Dereference Topic", "Fiscal Event Flattened Topic", "1234",
                "localhost", "Ifix Master Coa Context Path", "Ifix Master Coa Search Path", "localhost",
                "Ifix Master Government Context Path", "Ifix Master Government Search Path", "localhost",
                "Ifix Master Project Context Path", "Ifix Master Project Search Path", "localhost",
                "Ifix Master Expenditure Context Path", "Ifix Master Expenditure Search Path", "localhost",
                "Ifix Master Department Context Path", "Ifix Master Department Search Path");
        ObjectMapper objectMapper = new ObjectMapper();
        MappingJackson2HttpMessageConverter actualJacksonConverterResult = fiscalEventPostProcessorConfig
                .jacksonConverter(objectMapper);
        assertEquals(2, actualJacksonConverterResult.getSupportedMediaTypes().size());
        assertSame(objectMapper, actualJacksonConverterResult.getObjectMapper());
    }
}

