package org.egov.ifix.aggregate.repository;

import org.egov.ifix.aggregate.config.AbstractIT;
import org.egov.ifix.aggregate.config.ContainersEnvironment;
import org.egov.ifix.aggregate.config.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@SpringBootTest(classes = TestcontainersConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FiscalEventAggregateRepositoryTest extends AbstractIT {

    @InjectMocks
    private FiscalEventAggregateRepository fiscalEventAggregateRepository;

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    void testUpsert() {
        when(this.namedParameterJdbcTemplate.batchUpdate((String) any(),
                (org.springframework.jdbc.core.namedparam.SqlParameterSource[]) any())).thenReturn(new int[]{1, 1, 1, 1});
        int[] actualUpsertResult = this.fiscalEventAggregateRepository.upsert(new ArrayList<>());
        assertEquals(4, actualUpsertResult.length);
        assertEquals(1, actualUpsertResult[0]);
        assertEquals(1, actualUpsertResult[3]);
        verify(this.namedParameterJdbcTemplate).batchUpdate((String) any(),
                (org.springframework.jdbc.core.namedparam.SqlParameterSource[]) any());
    }
}

