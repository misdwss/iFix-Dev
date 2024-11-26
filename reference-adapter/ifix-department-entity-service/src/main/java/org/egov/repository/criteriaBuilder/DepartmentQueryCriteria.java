package org.egov.repository.criteriaBuilder;

import org.egov.web.models.persist.DepartmentEntity;
import org.egov.web.models.persist.DepartmentHierarchyLevel;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;


public class DepartmentQueryCriteria {
    private static String DEPARTMENT_ENTITY_SELECT_ALL = "SELECT * FROM department_entity";
    private static String DEPARTMENT_HIERARCHY_SELECT_ALL = "SELECT * FROM department_hierarchy_level";

    private StringBuilder criteriaQuery = new StringBuilder();

    private DepartmentQueryCriteria() {}

    public DepartmentQueryCriteria(StringBuilder criteriaQuery) {
        this.criteriaQuery = criteriaQuery;
    }

    public static DepartmentQueryCriteria builder(Class<? extends Object> entityClass) {

        if (DepartmentEntity.class.getTypeName().equals(entityClass.getTypeName())) {
            return new DepartmentQueryCriteria(new StringBuilder(DEPARTMENT_ENTITY_SELECT_ALL));
        }

        if (DepartmentHierarchyLevel.class.getTypeName().equals(entityClass.getTypeName())) {
            return new DepartmentQueryCriteria(new StringBuilder(DEPARTMENT_HIERARCHY_SELECT_ALL));
        }

        return null;
    }

    /**
     * @param attribute
     * @return
     */
    public DepartmentQueryCriteria where(String attribute) {
        if (!StringUtils.isEmpty(attribute)) {
            this.criteriaQuery.append(" WHERE " + attribute);
            return this;
        }

        return null;
    }

    public DepartmentQueryCriteria is(String attributeValue) {
        if (!StringUtils.isEmpty(attributeValue)) {
            this.criteriaQuery.append(" = '" + attributeValue + "'");
            return this;
        }
        return null;
    }

    public DepartmentQueryCriteria is(Integer attributeValue) {
        if (attributeValue != null) {
            this.criteriaQuery.append(" = " + attributeValue);
            return this;
        }
        return null;
    }

    public DepartmentQueryCriteria and(String attribute) {
        if (!StringUtils.isEmpty(attribute)) {
            this.criteriaQuery.append(" AND " + attribute);
            return this;
        }
        return null;
    }

    public DepartmentQueryCriteria in(List<String> attributeList) {
        if (attributeList != null && !attributeList.isEmpty()) {
            String inValues = attributeList.stream().collect(Collectors.joining("','", "'", "'"));

            this.criteriaQuery.append(" IN (" + inValues + ")");

            return this;
        }
        return null;
    }

    public DepartmentQueryCriteria like(String attributeValue) {
        if (!StringUtils.isEmpty(attributeValue)) {
            this.criteriaQuery.append(" like '%" + attributeValue + "%'");

            return this;
        }

        return null;
    }

    public String build() {
        return criteriaQuery.append(";").toString();
    }

}
