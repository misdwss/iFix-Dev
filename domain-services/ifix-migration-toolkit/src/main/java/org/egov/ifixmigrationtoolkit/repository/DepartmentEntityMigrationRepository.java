package org.egov.ifixmigrationtoolkit.repository;

import org.egov.ifixmigrationtoolkit.util.MigrationEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class DepartmentEntityMigrationRepository {

    private static final String RESUME_FROM_QUERY = "SELECT COALESCE( (SELECT pagenumber " +
            "FROM ifix_fiscal_events_migration WHERE service_type = '{SERVICE_TYPE}' " +
            "AND tenantid = '{TENANTID_PLACEHOLDER}' ORDER BY createdtime DESC LIMIT 1) , 0)";

    private static final String TOTAL_RECORDS_QUERY = "SELECT COALESCE( (SELECT totalnumberofrecordsmigrated " +
            "FROM ifix_fiscal_events_migration WHERE service_type = '{SERVICE_TYPE}' " +
            "AND tenantid = {TENANTID_PLACEHOLDER} ORDER BY createdtime DESC LIMIT 1) , 0)";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Integer getPageNumberToResumeFrom(String tenantId, MigrationEnum migrationEnum){
        RESUME_FROM_QUERY.replace("{TENANTID_PLACEHOLDER}", tenantId );
        RESUME_FROM_QUERY.replace("{SERVICE_TYPE}", migrationEnum.name());
        Integer resumeFrom = jdbcTemplate.queryForObject(RESUME_FROM_QUERY, Integer.class);
        return resumeFrom;
    }

    public Long getTotalNumberOfRecordsMigrated(String tenantId, MigrationEnum migrationEnum){
        TOTAL_RECORDS_QUERY.replace("{TENANTID_PLACEHOLDER}", tenantId );
        TOTAL_RECORDS_QUERY.replace("{SERVICE_TYPE}", migrationEnum.name());
        Long numberOfRecordsMigrated = jdbcTemplate.queryForObject(TOTAL_RECORDS_QUERY, Long.class);
        return numberOfRecordsMigrated;
    }
}
