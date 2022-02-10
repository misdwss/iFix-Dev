package org.egov.repository.queryBuilder;

import org.apache.commons.lang3.StringUtils;
import org.egov.web.models.ExpenditureSearchCriteria;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import static org.egov.util.MasterDataConstants.*;

/**
 *
 */
@Component
public class ExpenditureQueryBuilder {

    /**
     * @param expenditureSearchCriteria
     * @return
     */
    public Query buildQuerySearch(ExpenditureSearchCriteria expenditureSearchCriteria) {
        Criteria criteria = new Criteria();

        if (!StringUtils.isEmpty(expenditureSearchCriteria.getTenantId())) {
            criteria.and(CRITERIA_TENANT_ID).is(expenditureSearchCriteria.getTenantId());
        }

        if (!StringUtils.isEmpty(expenditureSearchCriteria.getName())) {
            criteria.and(CRITERIA_NAME).is(expenditureSearchCriteria.getName());
        }

        if (!StringUtils.isEmpty(expenditureSearchCriteria.getCode())) {
            criteria.and(CRITERIA_CODE).is(expenditureSearchCriteria.getCode());
        }

        if (expenditureSearchCriteria.getIds() != null && !expenditureSearchCriteria.getIds().isEmpty())
            criteria.and(CRITERIA_ID).in(expenditureSearchCriteria.getIds());

        return new Query(criteria);
    }
}
