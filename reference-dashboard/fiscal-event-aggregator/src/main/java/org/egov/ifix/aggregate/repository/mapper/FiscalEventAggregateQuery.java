package org.egov.ifix.aggregate.repository.mapper;


import org.springframework.stereotype.Component;

@Component
public class FiscalEventAggregateQuery {

    public static final String UPSERT_QUERY_FOR_FISCAL_EVENT_AGGREGATE = "INSERT INTO fiscal_event_aggregated" +
            "(ver,tenantId,government_id,government_name,type,sumAmount,fiscalPeriod,count,department_id,department_code," +
            "department_name,expenditure_id,expenditure_code,expenditure_name,expenditure_type,project_id,project_code,project_name,coa_id,coa_coaCode,coa_majorHead,coa_majorHeadName," +
            "coa_majorHeadType,coa_subMajorHead,coa_subMajorHeadName,coa_minorHead,coa_minorHeadName,coa_subHead,coa_subHeadName," +
            "coa_groupHead,coa_groupHeadName,coa_objectHead,coa_objectHeadName," +
            "departmentEntity_ancestry_0_id,departmentEntity_ancestry_0_code,departmentEntity_ancestry_0_name,"+
            "departmentEntity_ancestry_0_hierarchyLevel,departmentEntity_ancestry_1_id,departmentEntity_ancestry_1_code,"+
            "departmentEntity_ancestry_1_name,departmentEntity_ancestry_1_hierarchyLevel,departmentEntity_ancestry_2_id," +
            "departmentEntity_ancestry_2_code,departmentEntity_ancestry_2_name,departmentEntity_ancestry_2_hierarchyLevel," +
            "departmentEntity_ancestry_3_id,departmentEntity_ancestry_3_code,departmentEntity_ancestry_3_name," +
            "departmentEntity_ancestry_3_hierarchyLevel,departmentEntity_ancestry_4_id,departmentEntity_ancestry_4_code," +
            "departmentEntity_ancestry_4_name,departmentEntity_ancestry_4_hierarchyLevel,departmentEntity_ancestry_5_id," +
            "departmentEntity_ancestry_5_code,departmentEntity_ancestry_5_name,departmentEntity_ancestry_5_hierarchyLevel," +
            "departmentEntity_ancestry_6_id,departmentEntity_ancestry_6_code,departmentEntity_ancestry_6_name," +
            "departmentEntity_ancestry_6_hierarchyLevel,departmentEntity_ancestry_7_id,departmentEntity_ancestry_7_code," +
            "departmentEntity_ancestry_7_name,departmentEntity_ancestry_7_hierarchyLevel,departmentEntity_ancestry_8_id," +
            "departmentEntity_ancestry_8_code,departmentEntity_ancestry_8_name,departmentEntity_ancestry_8_hierarchyLevel," +
            "departmentEntity_ancestry_9_id,departmentEntity_ancestry_9_code,departmentEntity_ancestry_9_name," +
            "departmentEntity_ancestry_9_hierarchyLevel,departmentEntity_ancestry_10_id,departmentEntity_ancestry_10_code," +
            "departmentEntity_ancestry_10_name,departmentEntity_ancestry_10_hierarchyLevel,departmentEntity_id,departmentEntity_code," +
            "departmentEntity_name,departmentEntity_hierarchyLevel) " +
            "VALUES" +
            "(:ver,:tenantId,:government_id,:government_name,:type,:sumAmount,:fiscalPeriod,:count," +
            ":department_id,:department_code,:department_name,:expenditure_id,:expenditure_code,:expenditure_name," +
            ":expenditure_type,:project_id,:project_code,:project_name," +
            ":coa_id,:coa_coaCode,:coa_majorHead,:coa_majorHeadName,:coa_majorHeadType,:coa_subMajorHead,:coa_subMajorHeadName," +
            ":coa_minorHead,:coa_minorHeadName,:coa_subHead,:coa_subHeadName,:coa_groupHead,:coa_groupHeadName,:coa_objectHead," +
            ":coa_objectHeadName,:departmentEntity_ancestry_0_id,:departmentEntity_ancestry_0_code,:departmentEntity_ancestry_0_name," +
            ":departmentEntity_ancestry_0_hierarchyLevel,:departmentEntity_ancestry_1_id,:departmentEntity_ancestry_1_code," +
            ":departmentEntity_ancestry_1_name,:departmentEntity_ancestry_1_hierarchyLevel,:departmentEntity_ancestry_2_id," +
            ":departmentEntity_ancestry_2_code,:departmentEntity_ancestry_2_name,:departmentEntity_ancestry_2_hierarchyLevel," +
            ":departmentEntity_ancestry_3_id,:departmentEntity_ancestry_3_code,:departmentEntity_ancestry_3_name," +
            ":departmentEntity_ancestry_3_hierarchyLevel,:departmentEntity_ancestry_4_id,:departmentEntity_ancestry_4_code," +
            ":departmentEntity_ancestry_4_name,:departmentEntity_ancestry_4_hierarchyLevel,:departmentEntity_ancestry_5_id," +
            ":departmentEntity_ancestry_5_code,:departmentEntity_ancestry_5_name,:departmentEntity_ancestry_5_hierarchyLevel," +
            ":departmentEntity_ancestry_6_id,:departmentEntity_ancestry_6_code,:departmentEntity_ancestry_6_name," +
            ":departmentEntity_ancestry_6_hierarchyLevel,:departmentEntity_ancestry_7_id,:departmentEntity_ancestry_7_code," +
            ":departmentEntity_ancestry_7_name,:departmentEntity_ancestry_7_hierarchyLevel,:departmentEntity_ancestry_8_id," +
            ":departmentEntity_ancestry_8_code,:departmentEntity_ancestry_8_name,:departmentEntity_ancestry_8_hierarchyLevel," +
            ":departmentEntity_ancestry_9_id,:departmentEntity_ancestry_9_code,:departmentEntity_ancestry_9_name," +
            ":departmentEntity_ancestry_9_hierarchyLevel,:departmentEntity_ancestry_10_id,:departmentEntity_ancestry_10_code," +
            ":departmentEntity_ancestry_10_name,:departmentEntity_ancestry_10_hierarchyLevel," +
            ":departmentEntity_id,:departmentEntity_code,:departmentEntity_name,:departmentEntity_hierarchyLevel) " +
            "ON CONFLICT(project_id,coa_id,type,fiscalPeriod) " +
            "DO UPDATE " +
            "SET " +
            "ver=:ver,tenantId=:tenantId,government_id=:government_id,government_name=:government_name,sumAmount=:sumAmount,count=:count," +
            "department_id=:department_id,department_code=:department_code,department_name=:department_name,departmentEntity_ancestry_0_id=:departmentEntity_ancestry_0_id," +
            "expenditure_id=:expenditure_id,expenditure_code=:expenditure_code,expenditure_name=:expenditure_name,expenditure_type=:expenditure_type," +
            "project_code=:project_code,project_name=:project_name," +
            "coa_coaCode=:coa_coaCode,coa_majorHead=:coa_majorHead,coa_majorHeadName=:coa_majorHeadName,coa_majorHeadType=:coa_majorHeadType,coa_subMajorHead=:coa_subMajorHead," +
            "coa_subMajorHeadName=:coa_subMajorHeadName,coa_minorHead=:coa_minorHead,coa_minorHeadName=:coa_minorHeadName,coa_subHead=:coa_subHead," +
            "coa_subHeadName=:coa_subHeadName,coa_groupHead=:coa_groupHead,coa_groupHeadName=:coa_groupHeadName,coa_objectHead=:coa_objectHead,coa_objectHeadName=:coa_objectHeadName, " +
            "departmentEntity_ancestry_0_code=:departmentEntity_ancestry_0_code," +
            "departmentEntity_ancestry_0_name=:departmentEntity_ancestry_0_name,departmentEntity_ancestry_0_hierarchyLevel=:departmentEntity_ancestry_0_hierarchyLevel,departmentEntity_ancestry_1_id=:departmentEntity_ancestry_1_id," +
            "departmentEntity_ancestry_1_code=:departmentEntity_ancestry_1_code,departmentEntity_ancestry_1_name=:departmentEntity_ancestry_1_name,departmentEntity_ancestry_1_hierarchyLevel=:departmentEntity_ancestry_1_hierarchyLevel," +
            "departmentEntity_ancestry_2_id=:departmentEntity_ancestry_2_id,departmentEntity_ancestry_2_code=:departmentEntity_ancestry_2_code,departmentEntity_ancestry_2_name=:departmentEntity_ancestry_2_name," +
            "departmentEntity_ancestry_2_hierarchyLevel=:departmentEntity_ancestry_2_hierarchyLevel,departmentEntity_ancestry_3_id=:departmentEntity_ancestry_3_id,departmentEntity_ancestry_3_code=:departmentEntity_ancestry_3_code," +
            "departmentEntity_ancestry_3_name=:departmentEntity_ancestry_3_name,departmentEntity_ancestry_3_hierarchyLevel=:departmentEntity_ancestry_3_hierarchyLevel,departmentEntity_ancestry_4_id=:departmentEntity_ancestry_4_id," +
            "departmentEntity_ancestry_4_code=:departmentEntity_ancestry_4_code,departmentEntity_ancestry_4_name=:departmentEntity_ancestry_4_name,departmentEntity_ancestry_4_hierarchyLevel=:departmentEntity_ancestry_4_hierarchyLevel," +
            "departmentEntity_ancestry_5_id=:departmentEntity_ancestry_5_id,departmentEntity_ancestry_5_code=:departmentEntity_ancestry_5_code,departmentEntity_ancestry_5_name=:departmentEntity_ancestry_5_name," +
            "departmentEntity_ancestry_5_hierarchyLevel=:departmentEntity_ancestry_5_hierarchyLevel,departmentEntity_ancestry_6_id=:departmentEntity_ancestry_6_id,departmentEntity_ancestry_6_code=:departmentEntity_ancestry_6_code," +
            "departmentEntity_ancestry_6_name=:departmentEntity_ancestry_6_name,departmentEntity_ancestry_6_hierarchyLevel=:departmentEntity_ancestry_6_hierarchyLevel,departmentEntity_ancestry_7_id=:departmentEntity_ancestry_7_id," +
            "departmentEntity_ancestry_7_code=:departmentEntity_ancestry_7_code,departmentEntity_ancestry_7_name=:departmentEntity_ancestry_7_name,departmentEntity_ancestry_7_hierarchyLevel=:departmentEntity_ancestry_7_hierarchyLevel," +
            "departmentEntity_ancestry_8_id=:departmentEntity_ancestry_8_id,departmentEntity_ancestry_8_code=:departmentEntity_ancestry_8_code,departmentEntity_ancestry_8_name=:departmentEntity_ancestry_8_name," +
            "departmentEntity_ancestry_8_hierarchyLevel=:departmentEntity_ancestry_8_hierarchyLevel,departmentEntity_ancestry_9_id=:departmentEntity_ancestry_9_id,departmentEntity_ancestry_9_code=:departmentEntity_ancestry_9_code," +
            "departmentEntity_ancestry_9_name=:departmentEntity_ancestry_9_name,departmentEntity_ancestry_9_hierarchyLevel=:departmentEntity_ancestry_9_hierarchyLevel,departmentEntity_ancestry_10_id=:departmentEntity_ancestry_10_id," +
            "departmentEntity_ancestry_10_code=:departmentEntity_ancestry_10_code,departmentEntity_ancestry_10_name=:departmentEntity_ancestry_10_name,departmentEntity_ancestry_10_hierarchyLevel=:departmentEntity_ancestry_10_hierarchyLevel," +
            "departmentEntity_id=:departmentEntity_id,departmentEntity_code=:departmentEntity_code,departmentEntity_name=:departmentEntity_name,departmentEntity_hierarchyLevel=:departmentEntity_hierarchyLevel;";

//    public static final String INSERT_QUERY_FOR_FISCAL_EVENT_AGGREGATE = "INSERT INTO fiscal_event_aggregated" +
//            "(ver,tenantId,government_id,government_name,type,sumAmount,fiscalPeriod,count,department_id,department_code," +
//            "department_name,expenditure_id,expenditure_code,expenditure_name,expenditure_type,project_id,project_code,project_name,coa_id,coa_coaCode,coa_majorHead,coa_majorHeadName," +
//            "coa_majorHeadType,coa_subMajorHead,coa_subMajorHeadName,coa_minorHead,coa_minorHeadName,coa_subHead,coa_subHeadName," +
//            "coa_groupHead,coa_groupHeadName,coa_objectHead,coa_objectHeadName," +
//            "departmentEntity_ancestry_0_id,departmentEntity_ancestry_0_code,departmentEntity_ancestry_0_name,"+
//            "departmentEntity_ancestry_0_hierarchyLevel,departmentEntity_ancestry_1_id,departmentEntity_ancestry_1_code,"+
//            "departmentEntity_ancestry_1_name,departmentEntity_ancestry_1_hierarchyLevel,departmentEntity_ancestry_2_id," +
//            "departmentEntity_ancestry_2_code,departmentEntity_ancestry_2_name,departmentEntity_ancestry_2_hierarchyLevel," +
//            "departmentEntity_ancestry_3_id,departmentEntity_ancestry_3_code,departmentEntity_ancestry_3_name," +
//            "departmentEntity_ancestry_3_hierarchyLevel,departmentEntity_ancestry_4_id,departmentEntity_ancestry_4_code," +
//            "departmentEntity_ancestry_4_name,departmentEntity_ancestry_4_hierarchyLevel,departmentEntity_ancestry_5_id," +
//            "departmentEntity_ancestry_5_code,departmentEntity_ancestry_5_name,departmentEntity_ancestry_5_hierarchyLevel," +
//            "departmentEntity_ancestry_6_id,departmentEntity_ancestry_6_code,departmentEntity_ancestry_6_name," +
//            "departmentEntity_ancestry_6_hierarchyLevel,departmentEntity_ancestry_7_id,departmentEntity_ancestry_7_code," +
//            "departmentEntity_ancestry_7_name,departmentEntity_ancestry_7_hierarchyLevel,departmentEntity_ancestry_8_id," +
//            "departmentEntity_ancestry_8_code,departmentEntity_ancestry_8_name,departmentEntity_ancestry_8_hierarchyLevel," +
//            "departmentEntity_ancestry_9_id,departmentEntity_ancestry_9_code,departmentEntity_ancestry_9_name," +
//            "departmentEntity_ancestry_9_hierarchyLevel,departmentEntity_ancestry_10_id,departmentEntity_ancestry_10_code," +
//            "departmentEntity_ancestry_10_name,departmentEntity_ancestry_10_hierarchyLevel) " +
//            "VALUES" +
//            "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
//            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ;" ;
}
