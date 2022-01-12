package org.egov.ifix.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChartOfAccountMapRepository extends JpaRepository<ChartOfAccountMap, Long> {
    public ChartOfAccountMap findByClientCode(String clientCode);

    public List<ChartOfAccountMap> findByClientCodeAndTenantId(String clientCode, String tenantId);


}