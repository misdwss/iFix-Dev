package org.egov.repository.rowmapper;


import org.egov.web.models.persist.Project;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProjectRowMapper implements ResultSetExtractor<List<Project>> {
    @Override
    public List<Project> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<Project> projectList = new ArrayList<>();

        while (resultSet.next()) {
            projectList.add(
                    Project.builder()
                            .id(resultSet.getString("id"))
                            .tenantId(resultSet.getString("tenant_id"))
                            .code(resultSet.getString("code"))
                            .name(resultSet.getString("name"))
                            .expenditureId(resultSet.getString("expenditure_id"))
                            .createdBy(resultSet.getString("created_by"))
                            .createdTime(resultSet.getLong("created_time"))
                            .lastModifiedBy(resultSet.getString("last_modified_by"))
                            .lastModifiedTime(resultSet.getLong("last_modified_time"))
                            .build()
            );
        }

        return projectList;
    }
}
