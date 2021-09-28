package org.egov.ifix.aggregate.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.ifix.aggregate.config.ConfigProperties;
import org.egov.ifix.aggregate.model.FiscalEventAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Slf4j
@Component
public class FiscalEventAggregateUtil {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConfigProperties configProperties;

    /**
     * @param distinctProjectResponses
     * @return
     */
    public Map<String, JsonNode> getProjectDetailsMap(List<Object> distinctProjectResponses) {
        JsonNode responseJsonNode = objectMapper.convertValue(distinctProjectResponses, JsonNode.class);
        Iterator<JsonNode> nodeIterator = responseJsonNode.iterator();
        Map<String, JsonNode> projectNodeMap = new HashMap<>();
        while (nodeIterator.hasNext()) {
            JsonNode node = nodeIterator.next();
            if (node != null && !node.isEmpty() && node.get("event") != null) {
                JsonNode eventNode = node.get("event");
                if (eventNode.get("project.id") != null) {
                    projectNodeMap.put(eventNode.get("project.id").asText(), eventNode);
                }
            }
        }
        return projectNodeMap;
    }

    /**
     * @param distinctCoaIdResponses
     * @return
     */
    public Map<String, JsonNode> getCOADetailsMap(List<Object> distinctCoaIdResponses) {
        JsonNode responseJsonNode = objectMapper.convertValue(distinctCoaIdResponses, JsonNode.class);
        Iterator<JsonNode> nodeIterator = responseJsonNode.iterator();
        Map<String, JsonNode> coaNodeMap = new HashMap<>();
        while (nodeIterator.hasNext()) {
            JsonNode node = nodeIterator.next();
            if (node != null && !node.isEmpty() && node.get("event") != null) {
                JsonNode eventNode = node.get("event");
                if (eventNode.get("coa.id") != null) {
                    coaNodeMap.put(eventNode.get("coa.id").asText(), eventNode);
                }
            }
        }
        return coaNodeMap;
    }

    public Map<String, Integer> getIntervalYearMap() {
        Map<String, Integer> intervalYearMap = new HashMap<>();

        String[] defaultFiscalPeriodArr = (FiscalEventAggregateConstants.DEFAULT_FISCAL_PERIOD).split("-");
        String prefixYear = defaultFiscalPeriodArr[0].substring(0, 2);
        int startYear = Integer.parseInt(defaultFiscalPeriodArr[0]);
        int endYear = Integer.parseInt(prefixYear.concat(defaultFiscalPeriodArr[1]));
        if (StringUtils.isNotBlank(configProperties.getFiscalPeriod())) {
            String[] strArr = configProperties.getFiscalPeriod().split("-");
            prefixYear = strArr[0].substring(0, 2);
            startYear = Integer.parseInt(strArr[0]);
            endYear = Integer.parseInt(strArr[1] != null && strArr[1].length() == 4 ? strArr[1] : prefixYear.concat(strArr[1]));
        }
        intervalYearMap.put(FiscalEventAggregateConstants.START_YEAR, startYear);
        intervalYearMap.put(FiscalEventAggregateConstants.END_YEAR, endYear);
        return intervalYearMap;
    }

    /**
     * @param groupByResponses
     * @param projectNodeMap
     * @param coaNodeMap
     * @return
     */
    public List<FiscalEventAggregate> getFiscalEventAggregateData(List<Object> groupByResponses, Map<String, JsonNode> projectNodeMap, Map<String, JsonNode> coaNodeMap) {
        List<FiscalEventAggregate> fiscalEventAggregateList = new ArrayList<>();
        JsonNode responseJsonNode = objectMapper.convertValue(groupByResponses, JsonNode.class);
        Iterator<JsonNode> nodeIterator = responseJsonNode.iterator();
        while (nodeIterator.hasNext()) {
            JsonNode node = nodeIterator.next();
            if (node != null && !node.isEmpty() && node.get("event") != null) {
                JsonNode eventNode = node.get("event");
                FiscalEventAggregate eventAggregate = new FiscalEventAggregate();

                BigDecimal amount = eventNode.get("amount") != null ? eventNode.get("amount").decimalValue() : null;
                BigInteger count = eventNode.get("Count") != null ? eventNode.get("Count").bigIntegerValue() : null;
                String projectId = eventNode.get("project.id") != null ? eventNode.get("project.id").asText() : null;
                String coaId = eventNode.get("coa.id") != null ? eventNode.get("coa.id").asText() : null;
                String eventType = eventNode.get("eventType") != null ? eventNode.get("eventType").asText() : null;

                eventAggregate.setProject_id(projectId);
                eventAggregate.setSumAmount(amount);
                eventAggregate.setCount(count);
                eventAggregate.setCoa_id(coaId);
                eventAggregate.setType(eventType);

                eventAggregate.setVer(FiscalEventAggregateConstants.VER);
                String fiscalPeriod = configProperties.getFiscalPeriod() != null ? configProperties.getFiscalPeriod() : FiscalEventAggregateConstants.DEFAULT_FISCAL_PERIOD;
                eventAggregate.setFiscalPeriod(fiscalPeriod);
                //set the project details to fiscal event aggregate
                setProjectDetailsToFiscalEventAggregate(projectNodeMap, eventAggregate, projectId);

                //set the coa details to fiscal event aggregate
                setCoaDetailsToFiscalEventAggregate(coaNodeMap, eventAggregate, coaId);

                //TODO : push to kafka
                fiscalEventAggregateList.add(eventAggregate);
            }
        }
        return fiscalEventAggregateList;
    }

    private void setCoaDetailsToFiscalEventAggregate(Map<String, JsonNode> coaNodeMap, FiscalEventAggregate eventAggregate, String coaId) {
        if (coaNodeMap.containsKey(coaId)) {
            JsonNode coaEventNode = coaNodeMap.get(coaId);
            if (coaEventNode != null && !coaEventNode.isEmpty()) {
                eventAggregate.setCoa_coaCode(coaEventNode.get("coa.coaCode") != null
                        ? coaEventNode.get("coa.coaCode").asText() : null);
                eventAggregate.setCoa_groupHead(coaEventNode.get("coa.groupHead") != null
                        ? coaEventNode.get("coa.groupHead").asText() : null);
                eventAggregate.setCoa_groupHeadName(coaEventNode.get("coa.groupHeadName") != null
                        ? coaEventNode.get("coa.groupHeadName").asText() : null);
                eventAggregate.setCoa_majorHead(coaEventNode.get("coa.majorHead") != null
                        ? coaEventNode.get("coa.majorHead").asText() : null);
                eventAggregate.setCoa_majorHeadName(coaEventNode.get("coa.majorHeadName") != null
                        ? coaEventNode.get("coa.majorHeadName").asText() : null);

                eventAggregate.setCoa_minorHead(coaEventNode.get("coa.minorHead") != null
                        ? coaEventNode.get("coa.minorHead").asText() : null);
                eventAggregate.setCoa_minorHeadName(coaEventNode.get("coa.minorHeadName") != null
                        ? coaEventNode.get("coa.minorHeadName").asText() : null);
                eventAggregate.setCoa_objectHead(coaEventNode.get("coa.objectHead") != null
                        ? coaEventNode.get("coa.objectHead").asText() : null);
                eventAggregate.setCoa_objectHeadName(coaEventNode.get("coa.objectHeadName") != null
                        ? coaEventNode.get("coa.objectHeadName").asText() : null);

                eventAggregate.setCoa_subHead(coaEventNode.get("coa.subHead") != null
                        ? coaEventNode.get("coa.subHead").asText() : null);
                eventAggregate.setCoa_subHeadName(coaEventNode.get("coa.subHeadName") != null
                        ? coaEventNode.get("coa.subHeadName").asText() : null);
                eventAggregate.setCoa_subMajorHead(coaEventNode.get("coa.subMajorHead") != null
                        ? coaEventNode.get("coa.subMajorHead").asText() : null);
                eventAggregate.setCoa_subMajorHeadName(coaEventNode.get("coa.subMajorHeadName") != null
                        ? coaEventNode.get("coa.subMajorHeadName").asText() : null);

            }
        }
    }

    private void setProjectDetailsToFiscalEventAggregate(Map<String, JsonNode> projectNodeMap, FiscalEventAggregate eventAggregate, String projectId) {
        if (projectNodeMap.containsKey(projectId)) {
            JsonNode projectEventNode = projectNodeMap.get(projectId);
            if (projectEventNode != null && !projectEventNode.isEmpty()) {
                eventAggregate.setDepartment_code(projectEventNode.get("department.code") != null
                        ? projectEventNode.get("department.code").asText() : null);
                eventAggregate.setDepartment_id(projectEventNode.get("department.id") != null
                        ? projectEventNode.get("department.id").asText() : null);
                eventAggregate.setDepartment_name(projectEventNode.get("department.name") != null
                        ? projectEventNode.get("department.name").asText() : null);

                eventAggregate.setDepartmentEntity_code(projectEventNode.get("departmentEntity.code") != null
                        ? projectEventNode.get("departmentEntity.code").asText() : null);
                eventAggregate.setDepartmentEntity_hierarchyLevel(projectEventNode.get("departmentEntity.hierarchyLevel") != null
                        ? projectEventNode.get("departmentEntity.hierarchyLevel").asInt() : null);
                eventAggregate.setDepartmentEntity_id(projectEventNode.get("departmentEntity.id") != null
                        ? projectEventNode.get("departmentEntity.id").asText() : null);
                eventAggregate.setDepartmentEntity_name(projectEventNode.get("departmentEntity.name") != null
                        ? projectEventNode.get("departmentEntity.name").asText() : null);

                eventAggregate.setDepartmentEntity_ancestry_0_code(projectEventNode.get("departmentEntity.ancestry[0].code") != null
                        ? projectEventNode.get("departmentEntity.ancestry[0].code").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_0_hierarchyLevel(projectEventNode.get("departmentEntity.ancestry[0].hierarchyLevel") != null
                        ? projectEventNode.get("departmentEntity.ancestry[0].hierarchyLevel").asInt() : null);
                eventAggregate.setDepartmentEntity_ancestry_0_id(projectEventNode.get("departmentEntity.ancestry[0].id") != null
                        ? projectEventNode.get("departmentEntity.ancestry[0].id").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_0_name(projectEventNode.get("departmentEntity.ancestry[0].name") != null
                        ? projectEventNode.get("departmentEntity.ancestry[0].name").asText() : null);

                eventAggregate.setDepartmentEntity_ancestry_1_code(projectEventNode.get("departmentEntity.ancestry[1].code") != null
                        ? projectEventNode.get("departmentEntity.ancestry[1].code").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_1_hierarchyLevel(projectEventNode.get("departmentEntity.ancestry[1].hierarchyLevel") != null
                        ? projectEventNode.get("departmentEntity.ancestry[1].hierarchyLevel").asInt() : null);
                eventAggregate.setDepartmentEntity_ancestry_1_id(projectEventNode.get("departmentEntity.ancestry[1].id") != null
                        ? projectEventNode.get("departmentEntity.ancestry[1].id").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_1_name(projectEventNode.get("departmentEntity.ancestry[1].name") != null
                        ? projectEventNode.get("departmentEntity.ancestry[1].name").asText() : null);

                eventAggregate.setDepartmentEntity_ancestry_2_code(projectEventNode.get("departmentEntity.ancestry[2].code") != null
                        ? projectEventNode.get("departmentEntity.ancestry[2].code").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_2_hierarchyLevel(projectEventNode.get("departmentEntity.ancestry[2].hierarchyLevel") != null
                        ? projectEventNode.get("departmentEntity.ancestry[2].hierarchyLevel").asInt() : null);
                eventAggregate.setDepartmentEntity_ancestry_2_id(projectEventNode.get("departmentEntity.ancestry[2].id") != null
                        ? projectEventNode.get("departmentEntity.ancestry[2].id").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_2_name(projectEventNode.get("departmentEntity.ancestry[2].name") != null
                        ? projectEventNode.get("departmentEntity.ancestry[2].name").asText() : null);

                eventAggregate.setDepartmentEntity_ancestry_3_code(projectEventNode.get("departmentEntity.ancestry[3].code") != null
                        ? projectEventNode.get("departmentEntity.ancestry[3].code").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_3_hierarchyLevel(projectEventNode.get("departmentEntity.ancestry[3].hierarchyLevel") != null
                        ? projectEventNode.get("departmentEntity.ancestry[3].hierarchyLevel").asInt() : null);
                eventAggregate.setDepartmentEntity_ancestry_3_id(projectEventNode.get("departmentEntity.ancestry[3].id") != null
                        ? projectEventNode.get("departmentEntity.ancestry[3].id").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_3_name(projectEventNode.get("departmentEntity.ancestry[3].name") != null
                        ? projectEventNode.get("departmentEntity.ancestry[3].name").asText() : null);

                eventAggregate.setDepartmentEntity_ancestry_4_code(projectEventNode.get("departmentEntity.ancestry[4].code") != null
                        ? projectEventNode.get("departmentEntity.ancestry[4].code").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_4_hierarchyLevel(projectEventNode.get("departmentEntity.ancestry[4].hierarchyLevel") != null
                        ? projectEventNode.get("departmentEntity.ancestry[4].hierarchyLevel").asInt() : null);
                eventAggregate.setDepartmentEntity_ancestry_4_id(projectEventNode.get("departmentEntity.ancestry[4].id") != null
                        ? projectEventNode.get("departmentEntity.ancestry[4].id").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_4_name(projectEventNode.get("departmentEntity.ancestry[4].name") != null
                        ? projectEventNode.get("departmentEntity.ancestry[4].name").asText() : null);

                eventAggregate.setDepartmentEntity_ancestry_5_code(projectEventNode.get("departmentEntity.ancestry[5].code") != null
                        ? projectEventNode.get("departmentEntity.ancestry[5].code").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_5_hierarchyLevel(projectEventNode.get("departmentEntity.ancestry[5].hierarchyLevel") != null
                        ? projectEventNode.get("departmentEntity.ancestry[5].hierarchyLevel").asInt() : null);
                eventAggregate.setDepartmentEntity_ancestry_5_id(projectEventNode.get("departmentEntity.ancestry[5].id") != null
                        ? projectEventNode.get("departmentEntity.ancestry[5].id").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_5_name(projectEventNode.get("departmentEntity.ancestry[5].name") != null
                        ? projectEventNode.get("departmentEntity.ancestry[5].name").asText() : null);

                eventAggregate.setDepartmentEntity_ancestry_6_code(projectEventNode.get("departmentEntity.ancestry[6].code") != null
                        ? projectEventNode.get("departmentEntity.ancestry[6].code").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_6_hierarchyLevel(projectEventNode.get("departmentEntity.ancestry[6].hierarchyLevel") != null
                        ? projectEventNode.get("departmentEntity.ancestry[6].hierarchyLevel").asInt() : null);
                eventAggregate.setDepartmentEntity_ancestry_6_id(projectEventNode.get("departmentEntity.ancestry[6].id") != null
                        ? projectEventNode.get("departmentEntity.ancestry[6].id").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_6_name(projectEventNode.get("departmentEntity.ancestry[6].name") != null
                        ? projectEventNode.get("departmentEntity.ancestry[6].name").asText() : null);

                eventAggregate.setDepartmentEntity_ancestry_7_code(projectEventNode.get("departmentEntity.ancestry[7].code") != null
                        ? projectEventNode.get("departmentEntity.ancestry[7].code").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_7_hierarchyLevel(projectEventNode.get("departmentEntity.ancestry[7].hierarchyLevel") != null
                        ? projectEventNode.get("departmentEntity.ancestry[7].hierarchyLevel").asInt() : null);
                eventAggregate.setDepartmentEntity_ancestry_7_id(projectEventNode.get("departmentEntity.ancestry[7].id") != null
                        ? projectEventNode.get("departmentEntity.ancestry[7].id").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_7_name(projectEventNode.get("departmentEntity.ancestry[7].name") != null
                        ? projectEventNode.get("departmentEntity.ancestry[7].name").asText() : null);

                eventAggregate.setDepartmentEntity_ancestry_8_code(projectEventNode.get("departmentEntity.ancestry[8].code") != null
                        ? projectEventNode.get("departmentEntity.ancestry[8].code").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_8_hierarchyLevel(projectEventNode.get("departmentEntity.ancestry[8].hierarchyLevel") != null
                        ? projectEventNode.get("departmentEntity.ancestry[8].hierarchyLevel").asInt() : null);
                eventAggregate.setDepartmentEntity_ancestry_8_id(projectEventNode.get("departmentEntity.ancestry[8].id") != null
                        ? projectEventNode.get("departmentEntity.ancestry[8].id").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_8_name(projectEventNode.get("departmentEntity.ancestry[8].name") != null
                        ? projectEventNode.get("departmentEntity.ancestry[8].name").asText() : null);

                eventAggregate.setDepartmentEntity_ancestry_9_code(projectEventNode.get("departmentEntity.ancestry[9].code") != null
                        ? projectEventNode.get("departmentEntity.ancestry[9].code").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_9_hierarchyLevel(projectEventNode.get("departmentEntity.ancestry[9].hierarchyLevel") != null
                        ? projectEventNode.get("departmentEntity.ancestry[9].hierarchyLevel").asInt() : null);
                eventAggregate.setDepartmentEntity_ancestry_9_id(projectEventNode.get("departmentEntity.ancestry[9].id") != null
                        ? projectEventNode.get("departmentEntity.ancestry[9].id").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_9_name(projectEventNode.get("departmentEntity.ancestry[9].name") != null
                        ? projectEventNode.get("departmentEntity.ancestry[9].name").asText() : null);

                eventAggregate.setDepartmentEntity_ancestry_10_code(projectEventNode.get("departmentEntity.ancestry[10].code") != null
                        ? projectEventNode.get("departmentEntity.ancestry[10].code").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_10_hierarchyLevel(projectEventNode.get("departmentEntity.ancestry[10].hierarchyLevel") != null
                        ? projectEventNode.get("departmentEntity.ancestry[10].hierarchyLevel").asInt() : null);
                eventAggregate.setDepartmentEntity_ancestry_10_id(projectEventNode.get("departmentEntity.ancestry[10].id") != null
                        ? projectEventNode.get("departmentEntity.ancestry[10].id").asText() : null);
                eventAggregate.setDepartmentEntity_ancestry_10_name(projectEventNode.get("departmentEntity.ancestry[10].name") != null
                        ? projectEventNode.get("departmentEntity.ancestry[10].name").asText() : null);

                eventAggregate.setExpenditure_code(projectEventNode.get("expenditure.code") != null
                        ? projectEventNode.get("expenditure.code").asText() : null);
                eventAggregate.setExpenditure_id(projectEventNode.get("expenditure.id") != null
                        ? projectEventNode.get("expenditure.id").asText() : null);
                eventAggregate.setExpenditure_name(projectEventNode.get("expenditure.name") != null
                        ? projectEventNode.get("expenditure.name").asText() : null);
                eventAggregate.setExpenditure_type(projectEventNode.get("expenditure.type") != null
                        ? projectEventNode.get("expenditure.type").asText() : null);

                eventAggregate.setGovernment_id(projectEventNode.get("government.id") != null
                        ? projectEventNode.get("government.id").asText() : null);
                eventAggregate.setGovernment_name(projectEventNode.get("government.name") != null
                        ? projectEventNode.get("government.name").asText() : null);

                eventAggregate.setTenantId(projectEventNode.get("tenantId") != null
                        ? projectEventNode.get("tenantId").asText() : null);

                eventAggregate.setProject_code(projectEventNode.get("project.code") != null
                        ? projectEventNode.get("project.code").asText() : null);
                eventAggregate.setProject_name(projectEventNode.get("project.name") != null
                        ? projectEventNode.get("project.name").asText() : null);

//                eventAggregate.setVer(projectEventNode.get("version") != null
//                        ? projectEventNode.get("version").asText() : null);
            }
        }
    }

    public Map<String, JsonNode> getEventTypeMap(List<Object> eventTypeResponses) {
        if (eventTypeResponses == null || eventTypeResponses.isEmpty()) {
            return Collections.emptyMap();
        }
        JsonNode responseJsonNode = objectMapper.convertValue(eventTypeResponses, JsonNode.class);
        Iterator<JsonNode> nodeIterator = responseJsonNode.iterator();
        Map<String, JsonNode> projectNodeMap = new HashMap<>();
        while (nodeIterator.hasNext()) {
            JsonNode node = nodeIterator.next();
            if (node != null && !node.isEmpty() && node.get("event") != null) {
                JsonNode eventNode = node.get("event");
                if (eventNode.get("project.id") != null) {
                    projectNodeMap.put(eventNode.get("project.id").asText(), eventNode);
                }
            }
        }
        return projectNodeMap;
    }

    /**
     *
     * @param firstEventTypeNodeMap - will be demand or bill
     * @param secondEventTypeNodeMap - will be receipt or payment
     * @param projectNodeMap
     * @param pendingEventType
     * @return
     */
    public List<FiscalEventAggregate> getPendingCollectionFiscalEventAggregatedData(Map<String, JsonNode> firstEventTypeNodeMap, Map<String, JsonNode> secondEventTypeNodeMap, Map<String, JsonNode> projectNodeMap, String pendingEventType) {
        List<FiscalEventAggregate> fiscalEventAggregateList = new ArrayList<>();

        Map<String, BigDecimal> pendingAmountMap = new HashMap<>();
        //pending collection Amount
        if (firstEventTypeNodeMap != null && !firstEventTypeNodeMap.isEmpty()) {
            for (String dPid : firstEventTypeNodeMap.keySet()) {
                boolean isAvail = false;
                JsonNode demandJsonNode = firstEventTypeNodeMap.get(dPid);
                BigDecimal dAmt = demandJsonNode.get("amount") != null ? demandJsonNode.get("amount").decimalValue() : BigDecimal.ZERO;
                for (String rPid : secondEventTypeNodeMap.keySet()) {
                    if (dPid.equalsIgnoreCase(rPid)) {
                        isAvail = true;
                        JsonNode receiptJsonNode = secondEventTypeNodeMap.get(rPid);
                        BigDecimal rAmt = receiptJsonNode.get("amount") != null ? receiptJsonNode.get("amount").decimalValue() : BigDecimal.ZERO;
                        pendingAmountMap.put(dPid, (dAmt.subtract(rAmt)));
                    }
                }
                if (!isAvail) {
                    pendingAmountMap.put(dPid, dAmt);
                }
            }
        }
        //In case any receipt project id that has no demand type
        if (!pendingAmountMap.isEmpty() && !secondEventTypeNodeMap.isEmpty()) {
            for (String rPid : secondEventTypeNodeMap.keySet()) {
                if (!pendingAmountMap.containsKey(rPid)) {
                    JsonNode receiptJsonNode = secondEventTypeNodeMap.get(rPid);
                    BigDecimal rAmt = receiptJsonNode.get("amount") != null ? receiptJsonNode.get("amount").decimalValue() : BigDecimal.ZERO;
                    pendingAmountMap.put(rPid, BigDecimal.ZERO.subtract(rAmt));
                }
            }
        }

        //Create a fiscal event aggregated data for pending collections
        if(!pendingAmountMap.isEmpty()){
            for(String pid : pendingAmountMap.keySet()){
                FiscalEventAggregate pendingEventAggregate = new FiscalEventAggregate();

                pendingEventAggregate.setVer(FiscalEventAggregateConstants.VER);
                String fiscalPeriod = configProperties.getFiscalPeriod() != null ? configProperties.getFiscalPeriod() : FiscalEventAggregateConstants.DEFAULT_FISCAL_PERIOD;
                pendingEventAggregate.setFiscalPeriod(fiscalPeriod);
                pendingEventAggregate.setProject_id(pid);
                pendingEventAggregate.setCount(null);
                pendingEventAggregate.setSumAmount(pendingAmountMap.get(pid));
                pendingEventAggregate.setType(pendingEventType);

                //set the project details to fiscal event aggregate
                setProjectDetailsToFiscalEventAggregate(projectNodeMap, pendingEventAggregate, pid);

                //Handle the unique update on conflict
                pendingEventAggregate.setCoa_id("");
                fiscalEventAggregateList.add(pendingEventAggregate);
            }
        }

        return fiscalEventAggregateList;
    }
}
