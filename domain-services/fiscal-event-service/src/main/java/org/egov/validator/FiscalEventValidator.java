package org.egov.validator;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestHeader;
import org.egov.config.FiscalEventConfiguration;
import org.egov.repository.FiscalEventRepository;
import org.egov.tracer.model.CustomException;
import org.egov.util.*;
import org.egov.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class FiscalEventValidator {


    @Autowired
    private CoaUtil coaUtil;

    @Autowired
    TenantUtil tenantUtil;

    @Autowired
    ProjectUtil projectUtil;

    @Autowired
    private FiscalEventRepository eventRepository;

    @Autowired
    private FiscalEventConfiguration fiscalEventConfiguration;

    @Autowired
    private FiscalEventMapperUtil mapperUtil;

    /**
     * Validate the fiscal Event request
     *
     * @param fiscalEventRequest
     */
    public void validateFiscalEventPushPost(FiscalEventRequest fiscalEventRequest) {
        if (fiscalEventRequest != null && fiscalEventRequest.getRequestHeader() != null
                && fiscalEventRequest.getFiscalEvent() != null && !fiscalEventRequest.getFiscalEvent().isEmpty()) {
            Map<String, String> errorMap = new HashMap<>();

            //TODO - remove, if not needed
            if (StringUtils.isNotBlank(fiscalEventConfiguration.getFiscalEventPushReqMaxSize())) {
                int size = Integer.parseInt(fiscalEventConfiguration.getFiscalEventPushReqMaxSize());
                if (fiscalEventRequest.getFiscalEvent().size() > size) {
                    throw new CustomException("PUSH_DATA_SIZE", "Fiscal event push data size should be at most : " + size);
                }
            }

            for (FiscalEvent fiscalEvent : fiscalEventRequest.getFiscalEvent()) {
                boolean isMissing = false;
                if (StringUtils.isBlank(fiscalEvent.getReferenceId())) {
                    errorMap.put(MasterDataConstants.REFERENCE_ID, "Reference id is missing for project Id : " + fiscalEvent.getProjectId());
                    isMissing = true;
                }
                if (fiscalEvent.getEventType() == null
                        || !EnumUtils.isValidEnum(FiscalEvent.EventTypeEnum.class, fiscalEvent.getEventType().name())) {

                    errorMap.put(MasterDataConstants.EVENT_TYPE, "Fiscal event type is missing for project Id : " + fiscalEvent.getProjectId());
                    isMissing = true;
                }
                if (isMissing) {
                    break;
                }
            }
            validateReqHeader(fiscalEventRequest.getRequestHeader());
            validateTenantId(fiscalEventRequest, errorMap);
            validateProjectId(fiscalEventRequest, errorMap);
            validateFiscalEventAmountDetails(fiscalEventRequest, errorMap);
            validateParentEventId(fiscalEventRequest, errorMap);

            if (!errorMap.isEmpty()) {
                throw new CustomException(errorMap);
            }

        } else {
            throw new CustomException(MasterDataConstants.FISCAL_EVENT, "Fiscal event request data is not valid");
        }
    }

    private void validateParentEventId(FiscalEventRequest fiscalEventRequest, Map<String, String> errorMap) {
        if (fiscalEventRequest.getFiscalEvent() != null && !fiscalEventRequest.getFiscalEvent().isEmpty()
                && StringUtils.isNotBlank(fiscalEventRequest.getFiscalEvent().get(0).getTenantId())) {

            List<FiscalEvent> fiscalEvents = fiscalEventRequest.getFiscalEvent();
            Set<String> parentEventIds = new HashSet<>();
            String tenantId = fiscalEventRequest.getFiscalEvent().get(0).getTenantId();
            for (FiscalEvent fiscalEvent : fiscalEvents) {
                if (StringUtils.isNotBlank(fiscalEvent.getParentEventId())) {
                    parentEventIds.add(fiscalEvent.getParentEventId());
                }
            }
            if (!parentEventIds.isEmpty() && StringUtils.isNotBlank(tenantId)) {
                Criteria criteria = new Criteria();
                List<String> ids = new ArrayList<>(parentEventIds);
                criteria.setTenantId(tenantId);
                criteria.setIds(ids);

                List<Object> fiscalEventList = eventRepository.searchFiscalEvent(criteria);
                if (fiscalEventList == null || fiscalEventList.isEmpty()) {
                    errorMap.put(MasterDataConstants.PARENT_EVENT_ID, "Parent event id(s) : " + parentEventIds + " doesn't exist in the system.");
                } else if (!fiscalEventList.isEmpty() && (fiscalEventList.size() != parentEventIds.size())) {
                    List<FiscalEvent> dbFiscalEvents = mapperUtil.mapDereferencedFiscalEventToFiscalEvent(fiscalEventList);
                    Set<String> missingParentEventIds = new HashSet<>();
                    for (String parentEventId : parentEventIds) {
                        boolean isExist = false;
                        for (FiscalEvent fiscalEvent : dbFiscalEvents) {
                            if (fiscalEvent != null && StringUtils.isNotBlank(fiscalEvent.getParentEventId())
                                    && fiscalEvent.getParentEventId().equals(parentEventId)) {
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            missingParentEventIds.add(parentEventId);
                        }
                    }
                    if (!missingParentEventIds.isEmpty()) {
                        errorMap.put(MasterDataConstants.PARENT_EVENT_ID, "Parent event id(s) : " + missingParentEventIds + " doesn't exist in the system.");
                    }
                }
            }
        }

    }

    /**
     * Validate the request header
     *
     * @param requestHeader
     */
    private void validateReqHeader(RequestHeader requestHeader) {
        if (requestHeader == null)
            throw new CustomException("REQUEST_HEADER", "Request header is missing");
        if (requestHeader.getUserInfo() == null)
            throw new CustomException("USER_INFO", "User info is missing in request header");
        if (StringUtils.isBlank(requestHeader.getUserInfo().getUuid()))
            throw new CustomException("USER_INFO", "User info is missing in request header");
    }


    /**
     * Validate the fiscal event request - amount details(line items) attributes
     *
     * @param fiscalEventRequest
     * @param errorMap
     */
    private void validateFiscalEventAmountDetails(FiscalEventRequest fiscalEventRequest, Map<String, String> errorMap) {
        if (fiscalEventRequest.getFiscalEvent() != null && !fiscalEventRequest.getFiscalEvent().isEmpty()
                && StringUtils.isNotBlank(fiscalEventRequest.getFiscalEvent().get(0).getTenantId())) {

            String tenantId = fiscalEventRequest.getFiscalEvent().get(0).getTenantId();
            RequestHeader requestHeader = fiscalEventRequest.getRequestHeader();

            List<Amount> allAmountDetails = new ArrayList<>();
            for (FiscalEvent fiscalEvent : fiscalEventRequest.getFiscalEvent()) {
                List<Amount> amountDetails = fiscalEvent.getAmountDetails();
                if (amountDetails == null || amountDetails.isEmpty()) {
                    errorMap.put("AMOUNT_DETAILS", "Amount details are missing in the request");
                    return;
                }
                allAmountDetails.addAll(amountDetails);
            }

            Set<String> coaIds = new HashSet<>();
            for (Amount amount : allAmountDetails) {
                if (amount.getAmount() == null /*|| amount.getAmount().compareTo(BigDecimal.ZERO)==0*/)
                    errorMap.put("AMOUNT", "Amount is missing in request for coaId : " + amount.getCoaId());
                if (StringUtils.isNotBlank(amount.getCoaId()))
                    coaIds.add(amount.getCoaId());
                else
                    throw new CustomException("COA_ID", "COA id is missing in the request data");
            }

            List<String> responseCoaIds = coaUtil.getCOAIdsFromCOAService(requestHeader, coaIds, tenantId);

            List<String> errorCoaIds = new ArrayList<>();
            for (String coaId : coaIds) {
                if (!responseCoaIds.contains(coaId))
                    errorCoaIds.add(coaId);
            }

            if (!errorCoaIds.isEmpty()) {
                errorMap.put("COA_ID_INVALID", "This chart of account id : " + errorCoaIds + " is invalid " +
                        "(or) Combination of tenant id with this chart of account id doesn't exist");
            }
        }

    }

    /**
     * @param fiscalEventRequest
     * @param errorMap
     */
    public void validateTenantId(FiscalEventRequest fiscalEventRequest, Map<String, String> errorMap) {
        List<FiscalEvent> fiscalEvents = fiscalEventRequest.getFiscalEvent();
        Set<String> tenantIds = new HashSet<>();
        for (FiscalEvent fiscalEvent : fiscalEvents) {
            if (StringUtils.isBlank(fiscalEvent.getTenantId())) {
                errorMap.put(MasterDataConstants.TENANT_ID, "Tenant id is missing in request");
                return;
            }
            tenantIds.add(fiscalEvent.getTenantId());
        }

        if (tenantIds.size() > 1) {
            errorMap.put(MasterDataConstants.TENANT_ID, "Tenant id should be same in the request");
            return;
        }
        boolean isValidTenant = false;
        if (!tenantIds.isEmpty()) {
            isValidTenant = tenantUtil.validateTenant(new ArrayList<>(tenantIds), fiscalEventRequest.getRequestHeader());
        }
        if (!isValidTenant) {
            errorMap.put(MasterDataConstants.TENANT_ID, "Tenant id doesn't exist in the system");
        }
    }

    /**
     * @param fiscalEventRequest
     */
    public void validateProjectId(FiscalEventRequest fiscalEventRequest, Map<String, String> errorMap) {
        Set<String> projectIds = new HashSet<>();

        if (fiscalEventRequest != null && fiscalEventRequest.getFiscalEvent() != null
                && !fiscalEventRequest.getFiscalEvent().isEmpty()
                && StringUtils.isNotBlank(fiscalEventRequest.getFiscalEvent().get(0).getTenantId())) {
            for (FiscalEvent fiscalEvent : fiscalEventRequest.getFiscalEvent()) {
                if (StringUtils.isNotBlank(fiscalEvent.getProjectId())) {
                    projectIds.add(fiscalEvent.getProjectId());
                }
            }
        }

        Optional<JsonNode> optionalJsonNode = projectUtil.validateProjectId(fiscalEventRequest.getRequestHeader(), projectIds,
                fiscalEventRequest.getFiscalEvent().get(0).getTenantId());

        if (!optionalJsonNode.isPresent()) {
            errorMap.put(MasterDataConstants.PROJECT_ID, "Project ids don't exist in the system");
        } else if (projectIds.size() != optionalJsonNode.get().get("project").size()) {

            JsonNode projectListNode = optionalJsonNode.get().get("project");
            List<String> missingProjectIds = new ArrayList<>();

            for (String projectId : projectIds) {
                boolean isExist = false;
                if (projectListNode != null && !projectListNode.isEmpty()) {
                    for (JsonNode projectNode : projectListNode) {
                        if (projectNode.get("id") != null && projectNode.get("id").asText().equals(projectId)) {
                            isExist = true;
                            break;
                        }
                    }
                }
                if (!isExist) {
                    missingProjectIds.add(projectId);
                }
            }

            if (!missingProjectIds.isEmpty()) {
                errorMap.put(MasterDataConstants.PROJECT_ID, "Project id(s) : " + missingProjectIds + " don't exist in the system");
            }
        } else{
            JsonNode jsonNode = optionalJsonNode.get();
            JsonNode projectListNode = jsonNode.get("project");

            if (projectListNode != null && !projectListNode.isEmpty()) {

                for (JsonNode projectNode : projectListNode) {
                    if (projectNode.get("expenditureId") == null) {
                        errorMap.put(MasterDataConstants.PROJECT_ID_EXPENDITURE_ID, "Expenditure details is missing in project for project id : " + projectNode.get("id"));
                        break;
                    }

                    if (projectNode.get("departmentEntity") == null
                            || projectNode.get("departmentEntity").get("departmentId") == null) {
                        errorMap.put(MasterDataConstants.PROJECT_ID_DEPARTMENT_ENTITY_ID, "Department entity details doesn't exist in the projectfor project id : " + projectNode.get("id"));
                        break;
                    }
                }
            } else {
                errorMap.put(MasterDataConstants.PROJECT_ID, "Project doesn't exist in the system");
            }
        }
    }

    public void validateFiscalEventSearchPost(FiscalEventGetRequest fiscalEventGetRequest) {
        RequestHeader requestHeader = fiscalEventGetRequest.getRequestHeader();
        Criteria criteria = fiscalEventGetRequest.getCriteria();

        validateReqHeader(requestHeader);
        if (criteria == null)
            throw new CustomException(MasterDataConstants.SEARCH_CRITERIA, "Search criteria is missing.");

        Map<String, String> errorMap = new HashMap<>();

        //validation : tenant id
        if (StringUtils.isBlank(criteria.getTenantId()))
            errorMap.put(MasterDataConstants.TENANT_ID, "Tenant id is missing in request");
        boolean isValidTenant = false;
        if (StringUtils.isNotBlank(criteria.getTenantId())) {
            List<String> tenantIds = new ArrayList<>();
            tenantIds.add(criteria.getTenantId());
            isValidTenant = tenantUtil.validateTenant(tenantIds, requestHeader);
        }
        if (!isValidTenant)
            errorMap.put(MasterDataConstants.TENANT_ID, "Tenant id doesn't exist in the system");

        //validation : event type
        if (StringUtils.isBlank(criteria.getEventType()))
            errorMap.put(MasterDataConstants.EVENT_TYPE, "Event type is missing in request");

        //validation :  fromEventTime and toEventTime
        if (criteria.getFromEventTime() != null) {
            if (criteria.getToEventTime() == null) {
                errorMap.put(MasterDataConstants.TO_EVENT_TIME, "To(End) event time is missing for given From(start) event time in request.");
            }
        }
        if (criteria.getToEventTime() != null) {
            if (criteria.getFromEventTime() == null) {
                errorMap.put(MasterDataConstants.FROM_EVENT_TIME, "From(start) event time is missing for given To(End) event time in request.");
            }
        }
        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
    }
}
