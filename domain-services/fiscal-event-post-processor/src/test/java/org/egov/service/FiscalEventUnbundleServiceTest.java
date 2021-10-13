package org.egov.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.egov.common.contract.AuditDetails;
import org.egov.config.TestDataFormatter;
import org.egov.web.models.AmountDetailsDeReferenced;
import org.egov.web.models.Department;
import org.egov.web.models.DepartmentEntity;
import org.egov.web.models.Expenditure;
import org.egov.web.models.FiscalEventDeReferenced;
import org.egov.web.models.Government;
import org.egov.web.models.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class FiscalEventUnbundleServiceTest {

    @Autowired
    private TestDataFormatter testDataFormatter;

    @InjectMocks
    private FiscalEventUnbundleService fiscalEventUnbundleService;

    @Test
    void testUnbundle() {
        assertTrue(this.fiscalEventUnbundleService.unbundle(new FiscalEventDeReferenced()).isEmpty());
    }

    @Test
    void testUnbundle2() {
        Government government = new Government("42", "Name");

        Department department = new Department("42", "Code", "Name");

        DepartmentEntity departmentEntity = new DepartmentEntity();
        Expenditure expenditure = new Expenditure("42", "Code", "Name", Expenditure.TypeEnum.SCHEME);

        Project project = new Project("42", "Code", "Name");

        ArrayList<AmountDetailsDeReferenced> amountDetails = new ArrayList<AmountDetailsDeReferenced>();
        assertTrue(this.fiscalEventUnbundleService
                .unbundle(
                        new FiscalEventDeReferenced("1.0.2", "42", "42", government, department, departmentEntity, expenditure,
                                project, "Event Type", 1L, 1L, "42", "42", "42", amountDetails, new AuditDetails(), "Attributes"))
                .isEmpty());
    }

    @Test
    void testUnbundle3() {
        FiscalEventDeReferenced fiscalEventDeReferenced = new FiscalEventDeReferenced();
        fiscalEventDeReferenced.addAmountDetailsItem(new AmountDetailsDeReferenced());
        assertEquals(1, this.fiscalEventUnbundleService.unbundle(fiscalEventDeReferenced).size());
    }
}

