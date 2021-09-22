package org.egov.ifix.aggregate.repository.mapper;

import org.egov.ifix.aggregate.model.FiscalEventAggregate;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class FiscalEventAggregatedDataMapper implements BatchPreparedStatementSetter {

    private List<FiscalEventAggregate> fiscalEventAggregates = null;

    public FiscalEventAggregatedDataMapper(List<FiscalEventAggregate> fiscalEventAggregates) {
        this.fiscalEventAggregates = fiscalEventAggregates;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        FiscalEventAggregate fiscalEventAggregate = fiscalEventAggregates.get(i);

        ps.setString(1, fiscalEventAggregate.getVer());
        ps.setString(2, fiscalEventAggregate.getTenantId());
        ps.setString(3, fiscalEventAggregate.getGovernment_id());
        ps.setString(4, fiscalEventAggregate.getGovernment_name());
        ps.setString(5, fiscalEventAggregate.getType());
        ps.setBigDecimal(6, fiscalEventAggregate.getSumAmount());
        ps.setString(7, fiscalEventAggregate.getFiscalPeriod());
        ps.setLong(8, Long.parseLong(fiscalEventAggregate.getCount().toString()));//
        ps.setString(9, fiscalEventAggregate.getDepartment_id());
        ps.setString(10, fiscalEventAggregate.getDepartment_code());
        ps.setString(11, fiscalEventAggregate.getDepartment_name());

        ps.setString(12, fiscalEventAggregate.getExpenditure_id());
        ps.setString(13, fiscalEventAggregate.getExpenditure_code());
        ps.setString(14, fiscalEventAggregate.getExpenditure_name());
        ps.setString(15, fiscalEventAggregate.getExpenditure_type());

        ps.setString(16, fiscalEventAggregate.getProject_id());
        ps.setString(17, fiscalEventAggregate.getProject_code());
        ps.setString(18, fiscalEventAggregate.getProject_name());

        ps.setString(19, fiscalEventAggregate.getCoa_id());
        ps.setString(20, fiscalEventAggregate.getCoa_coaCode());
        ps.setString(21, fiscalEventAggregate.getCoa_majorHead());
        ps.setString(22, fiscalEventAggregate.getCoa_majorHeadName());
        ps.setString(23, fiscalEventAggregate.getCoa_majorHeadType());
        ps.setString(24, fiscalEventAggregate.getCoa_subMajorHead());
        ps.setString(25, fiscalEventAggregate.getCoa_subMajorHeadName());
        ps.setString(26, fiscalEventAggregate.getCoa_minorHead());
        ps.setString(27, fiscalEventAggregate.getCoa_minorHeadName());
        ps.setString(28, fiscalEventAggregate.getCoa_subHead());
        ps.setString(29, fiscalEventAggregate.getCoa_subHeadName());
        ps.setString(30, fiscalEventAggregate.getCoa_groupHead());
        ps.setString(31, fiscalEventAggregate.getCoa_groupHeadName());
        ps.setString(32, fiscalEventAggregate.getCoa_objectHead());
        ps.setString(33, fiscalEventAggregate.getCoa_objectHeadName());

        ps.setString(34, fiscalEventAggregate.getDepartmentEntity_ancestry_0_id());
        ps.setString(35, fiscalEventAggregate.getDepartmentEntity_ancestry_0_code());
        ps.setString(36, fiscalEventAggregate.getDepartmentEntity_ancestry_0_name());
        ps.setInt(37, fiscalEventAggregate.getDepartmentEntity_ancestry_0_hierarchyLevel());


        ps.setString(38, fiscalEventAggregate.getDepartmentEntity_ancestry_1_id());
        ps.setString(39, fiscalEventAggregate.getDepartmentEntity_ancestry_1_code());
        ps.setString(40, fiscalEventAggregate.getDepartmentEntity_ancestry_1_name());
        ps.setInt(41, fiscalEventAggregate.getDepartmentEntity_ancestry_1_hierarchyLevel());

        ps.setString(42, fiscalEventAggregate.getDepartmentEntity_ancestry_2_id());
        ps.setString(43, fiscalEventAggregate.getDepartmentEntity_ancestry_2_code());
        ps.setString(44, fiscalEventAggregate.getDepartmentEntity_ancestry_2_name());
        ps.setInt(45, fiscalEventAggregate.getDepartmentEntity_ancestry_2_hierarchyLevel());

        ps.setString(46, fiscalEventAggregate.getDepartmentEntity_ancestry_3_id());
        ps.setString(47, fiscalEventAggregate.getDepartmentEntity_ancestry_3_code());
        ps.setString(48, fiscalEventAggregate.getDepartmentEntity_ancestry_3_name());
        ps.setInt(49, fiscalEventAggregate.getDepartmentEntity_ancestry_3_hierarchyLevel());

        ps.setString(50, fiscalEventAggregate.getDepartmentEntity_ancestry_4_id());
        ps.setString(51, fiscalEventAggregate.getDepartmentEntity_ancestry_4_code());
        ps.setString(52, fiscalEventAggregate.getDepartmentEntity_ancestry_4_name());
        ps.setInt(53, fiscalEventAggregate.getDepartmentEntity_ancestry_4_hierarchyLevel());

        ps.setString(54, fiscalEventAggregate.getDepartmentEntity_ancestry_5_id());
        ps.setString(55, fiscalEventAggregate.getDepartmentEntity_ancestry_5_code());
        ps.setString(56, fiscalEventAggregate.getDepartmentEntity_ancestry_5_name());
        ps.setInt(57, fiscalEventAggregate.getDepartmentEntity_ancestry_5_hierarchyLevel());

        ps.setString(58, fiscalEventAggregate.getDepartmentEntity_ancestry_6_id());
        ps.setString(59, fiscalEventAggregate.getDepartmentEntity_ancestry_6_code());
        ps.setString(60, fiscalEventAggregate.getDepartmentEntity_ancestry_6_name());
        ps.setInt(61, fiscalEventAggregate.getDepartmentEntity_ancestry_6_hierarchyLevel());

        ps.setString(62, fiscalEventAggregate.getDepartmentEntity_ancestry_7_id());
        ps.setString(63, fiscalEventAggregate.getDepartmentEntity_ancestry_7_code());
        ps.setString(64, fiscalEventAggregate.getDepartmentEntity_ancestry_7_name());
        ps.setInt(65, fiscalEventAggregate.getDepartmentEntity_ancestry_7_hierarchyLevel() != null
                ? fiscalEventAggregate.getDepartmentEntity_ancestry_7_hierarchyLevel()
                : 7);

        ps.setString(66, fiscalEventAggregate.getDepartmentEntity_ancestry_8_id());
        ps.setString(67, fiscalEventAggregate.getDepartmentEntity_ancestry_8_code());
        ps.setString(68, fiscalEventAggregate.getDepartmentEntity_ancestry_8_name());
        ps.setInt(69, fiscalEventAggregate.getDepartmentEntity_ancestry_8_hierarchyLevel() != null
                ? fiscalEventAggregate.getDepartmentEntity_ancestry_8_hierarchyLevel() : 8);

        ps.setString(70, fiscalEventAggregate.getDepartmentEntity_ancestry_9_id());
        ps.setString(71, fiscalEventAggregate.getDepartmentEntity_ancestry_9_code());
        ps.setString(72, fiscalEventAggregate.getDepartmentEntity_ancestry_9_name());
        ps.setInt(73, fiscalEventAggregate.getDepartmentEntity_ancestry_9_hierarchyLevel() != null
                ? fiscalEventAggregate.getDepartmentEntity_ancestry_9_hierarchyLevel() : 9);

        ps.setString(74, fiscalEventAggregate.getDepartmentEntity_ancestry_10_id());
        ps.setString(75, fiscalEventAggregate.getDepartmentEntity_ancestry_10_code());
        ps.setString(76, fiscalEventAggregate.getDepartmentEntity_ancestry_10_name());
        ps.setInt(77, fiscalEventAggregate.getDepartmentEntity_ancestry_10_hierarchyLevel() != null
                ? fiscalEventAggregate.getDepartmentEntity_ancestry_10_hierarchyLevel() : 10);


        ps.setString(78, fiscalEventAggregate.getDepartmentEntity_id());
        ps.setString(79, fiscalEventAggregate.getDepartmentEntity_code());
        ps.setString(80, fiscalEventAggregate.getDepartmentEntity_name());
        ps.setInt(81, fiscalEventAggregate.getDepartmentEntity_hierarchyLevel() != null
                ? fiscalEventAggregate.getDepartmentEntity_hierarchyLevel() : 0);



        //Update on conflict
        ps.setString(82, fiscalEventAggregate.getVer());
        ps.setString(83, fiscalEventAggregate.getTenantId());
        ps.setString(84, fiscalEventAggregate.getGovernment_id());
        ps.setString(85, fiscalEventAggregate.getGovernment_name());
        //ps.setString(5, fiscalEventAggregate.getEventType());
        ps.setBigDecimal(86, fiscalEventAggregate.getSumAmount());
        //ps.setString(7, fiscalEventAggregate.getFiscalPeriod());
        ps.setLong(87, Long.parseLong(fiscalEventAggregate.getCount().toString()));//
        ps.setString(88, fiscalEventAggregate.getDepartment_id());
        ps.setString(89, fiscalEventAggregate.getDepartment_code());
        ps.setString(90, fiscalEventAggregate.getDepartment_name());

        ps.setString(91, fiscalEventAggregate.getExpenditure_id());
        ps.setString(92, fiscalEventAggregate.getExpenditure_code());
        ps.setString(93, fiscalEventAggregate.getExpenditure_name());
        ps.setString(94, fiscalEventAggregate.getExpenditure_type());

        //ps.setString(91, fiscalEventAggregate.getProject_id());
        ps.setString(95, fiscalEventAggregate.getProject_code());
        ps.setString(96, fiscalEventAggregate.getProject_name());

        //ps.setString(19, fiscalEventAggregate.getCoa_id());
        ps.setString(97, fiscalEventAggregate.getCoa_coaCode());
        ps.setString(98, fiscalEventAggregate.getCoa_majorHead());
        ps.setString(99, fiscalEventAggregate.getCoa_majorHeadName());
        ps.setString(100, fiscalEventAggregate.getCoa_majorHeadType());
        ps.setString(101, fiscalEventAggregate.getCoa_subMajorHead());
        ps.setString(102, fiscalEventAggregate.getCoa_subMajorHeadName());
        ps.setString(103, fiscalEventAggregate.getCoa_minorHead());
        ps.setString(104, fiscalEventAggregate.getCoa_minorHeadName());
        ps.setString(105, fiscalEventAggregate.getCoa_subHead());
        ps.setString(106, fiscalEventAggregate.getCoa_subHeadName());
        ps.setString(107, fiscalEventAggregate.getCoa_groupHead());
        ps.setString(108, fiscalEventAggregate.getCoa_groupHeadName());
        ps.setString(109, fiscalEventAggregate.getCoa_objectHead());
        ps.setString(110, fiscalEventAggregate.getCoa_objectHeadName());

        ps.setString(111, fiscalEventAggregate.getDepartmentEntity_ancestry_0_id());
        ps.setString(112, fiscalEventAggregate.getDepartmentEntity_ancestry_0_code());
        ps.setString(113, fiscalEventAggregate.getDepartmentEntity_ancestry_0_name());
        ps.setInt(114, fiscalEventAggregate.getDepartmentEntity_ancestry_0_hierarchyLevel());


        ps.setString(115, fiscalEventAggregate.getDepartmentEntity_ancestry_1_id());
        ps.setString(116, fiscalEventAggregate.getDepartmentEntity_ancestry_1_code());
        ps.setString(117, fiscalEventAggregate.getDepartmentEntity_ancestry_1_name());
        ps.setInt(118, fiscalEventAggregate.getDepartmentEntity_ancestry_1_hierarchyLevel());

        ps.setString(119, fiscalEventAggregate.getDepartmentEntity_ancestry_2_id());
        ps.setString(120, fiscalEventAggregate.getDepartmentEntity_ancestry_2_code());
        ps.setString(121, fiscalEventAggregate.getDepartmentEntity_ancestry_2_name());
        ps.setInt(122, fiscalEventAggregate.getDepartmentEntity_ancestry_2_hierarchyLevel());

        ps.setString(123, fiscalEventAggregate.getDepartmentEntity_ancestry_3_id());
        ps.setString(124, fiscalEventAggregate.getDepartmentEntity_ancestry_3_code());
        ps.setString(125, fiscalEventAggregate.getDepartmentEntity_ancestry_3_name());
        ps.setInt(126, fiscalEventAggregate.getDepartmentEntity_ancestry_3_hierarchyLevel());

        ps.setString(127, fiscalEventAggregate.getDepartmentEntity_ancestry_4_id());
        ps.setString(128, fiscalEventAggregate.getDepartmentEntity_ancestry_4_code());
        ps.setString(129, fiscalEventAggregate.getDepartmentEntity_ancestry_4_name());
        ps.setInt(130, fiscalEventAggregate.getDepartmentEntity_ancestry_4_hierarchyLevel());

        ps.setString(131, fiscalEventAggregate.getDepartmentEntity_ancestry_5_id());
        ps.setString(132, fiscalEventAggregate.getDepartmentEntity_ancestry_5_code());
        ps.setString(133, fiscalEventAggregate.getDepartmentEntity_ancestry_5_name());
        ps.setInt(134, fiscalEventAggregate.getDepartmentEntity_ancestry_5_hierarchyLevel());

        ps.setString(135, fiscalEventAggregate.getDepartmentEntity_ancestry_6_id());
        ps.setString(136, fiscalEventAggregate.getDepartmentEntity_ancestry_6_code());
        ps.setString(137, fiscalEventAggregate.getDepartmentEntity_ancestry_6_name());
        ps.setInt(138, fiscalEventAggregate.getDepartmentEntity_ancestry_6_hierarchyLevel());

        ps.setString(139, fiscalEventAggregate.getDepartmentEntity_ancestry_7_id());
        ps.setString(140, fiscalEventAggregate.getDepartmentEntity_ancestry_7_code());
        ps.setString(141, fiscalEventAggregate.getDepartmentEntity_ancestry_7_name());
        ps.setInt(142, fiscalEventAggregate.getDepartmentEntity_ancestry_7_hierarchyLevel() != null
                ? fiscalEventAggregate.getDepartmentEntity_ancestry_7_hierarchyLevel()
                : 7);

        ps.setString(143, fiscalEventAggregate.getDepartmentEntity_ancestry_8_id());
        ps.setString(144, fiscalEventAggregate.getDepartmentEntity_ancestry_8_code());
        ps.setString(145, fiscalEventAggregate.getDepartmentEntity_ancestry_8_name());
        ps.setInt(146, fiscalEventAggregate.getDepartmentEntity_ancestry_8_hierarchyLevel() != null
                ? fiscalEventAggregate.getDepartmentEntity_ancestry_8_hierarchyLevel() : 8);

        ps.setString(147, fiscalEventAggregate.getDepartmentEntity_ancestry_9_id());
        ps.setString(148, fiscalEventAggregate.getDepartmentEntity_ancestry_9_code());
        ps.setString(149, fiscalEventAggregate.getDepartmentEntity_ancestry_9_name());
        ps.setInt(150, fiscalEventAggregate.getDepartmentEntity_ancestry_9_hierarchyLevel() != null
                ? fiscalEventAggregate.getDepartmentEntity_ancestry_9_hierarchyLevel() : 9);

        ps.setString(151, fiscalEventAggregate.getDepartmentEntity_ancestry_10_id());
        ps.setString(152, fiscalEventAggregate.getDepartmentEntity_ancestry_10_code());
        ps.setString(153, fiscalEventAggregate.getDepartmentEntity_ancestry_10_name());
        ps.setInt(154, fiscalEventAggregate.getDepartmentEntity_ancestry_10_hierarchyLevel() != null
                ? fiscalEventAggregate.getDepartmentEntity_ancestry_10_hierarchyLevel() : 10);

        ps.setString(155, fiscalEventAggregate.getDepartmentEntity_id());
        ps.setString(156, fiscalEventAggregate.getDepartmentEntity_code());
        ps.setString(157, fiscalEventAggregate.getDepartmentEntity_name());
        ps.setInt(158, fiscalEventAggregate.getDepartmentEntity_hierarchyLevel() != null
                ? fiscalEventAggregate.getDepartmentEntity_hierarchyLevel() : 0);
    }

    @Override
    public int getBatchSize() {
        return fiscalEventAggregates.size();
    }
}
