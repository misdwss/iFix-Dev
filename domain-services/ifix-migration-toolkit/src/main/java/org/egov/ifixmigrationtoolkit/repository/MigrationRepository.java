package org.egov.ifixmigrationtoolkit.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MigrationRepository {

    private static final String RESUME_FROM_QUERY = "SELECT COALESCE( (SELECT resumefrom FROM ifix_migration_progress WHERE tenantid = {TENANTID_PLACEHOLDER} ORDER BY createdtime DESC LIMIT 1\n" +
            ") , 0)";
    /*
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Integer getOffsetToResumeFrom(String tenantId){
        Integer resumeFrom = jdbcTemplate.queryForObject(RESUME_FROM_QUERY.replace("{TENANTID_PLACEHOLDER}", "'" + tenantId + "'"), Integer.class);
        return resumeFrom;
    }
     */
}
