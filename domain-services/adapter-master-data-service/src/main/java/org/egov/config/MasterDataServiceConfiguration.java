package org.egov.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Component
@Data
@Import({TracerConfiguration.class})
@NoArgsConstructor
@AllArgsConstructor
public class MasterDataServiceConfiguration {

    @Value("${app.timezone}")
    private String timeZone;

    @PostConstruct
    public void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }

    @Value("${ifix.master.expenditure.host}")
    private String ifixMasterExpenditureHost;

    @Value("${ifix.master.expenditure.context.path}")
    private String ifixMasterExpenditureContextPath;

    @Value("${ifix.master.expenditure.search.path}")
    private String ifixMasterExpenditureSearchPath;

    @Value("${ifix.department.entity.host}")
    private String departmentEntityHost;

    @Value("${ifix.department.entity.context.path}")
    private String departmentEntityContextPath;

    @Value("${ifix.department.entity.search.path}")
    private String departmentEntitySearchPath;

    @Value("${ifix.master.department.host}")
    private String ifixMasterDepartmentHost;

    @Value("${ifix.master.department.context.path}")
    private String ifixMasterDepartmentContextPath;

    @Value("${ifix.master.department.search.path}")
    private String ifixMasterDepartmentSearchPath;

}
