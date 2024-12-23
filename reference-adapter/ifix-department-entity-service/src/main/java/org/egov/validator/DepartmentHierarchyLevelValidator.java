package org.egov.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestHeader;
import org.egov.repository.DepartmentHierarchyLevelRepository;
import org.egov.tracer.model.CustomException;
import org.egov.util.DepartmentEntityConstant;
import org.egov.util.DepartmentUtil;
import org.egov.web.models.DepartmentHierarchyLevel;
import org.egov.web.models.DepartmentHierarchyLevelRequest;
import org.egov.web.models.DepartmentHierarchyLevelSearchCriteria;
import org.egov.web.models.DepartmentHierarchyLevelSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DepartmentHierarchyLevelValidator {

    @Autowired
    private DepartmentUtil departmentUtil;

    @Autowired
    private DepartmentHierarchyLevelRepository levelRepository;

    public void validateHierarchyLevelCreatePost(DepartmentHierarchyLevelRequest request) {
        DepartmentHierarchyLevel departmentHierarchyLevel = request.getDepartmentHierarchyLevel();
        RequestHeader requestHeader = request.getRequestHeader();
        Map<String, String> errorMap = new HashMap<>();

        //Header validation
        if (requestHeader == null) {
            throw new CustomException(DepartmentEntityConstant.ERROR_REQUEST_HEADER, "Request header is missing");
        }
        if (requestHeader.getUserInfo() == null || requestHeader.getUserInfo().getUuid() == null) {
            errorMap.put(DepartmentEntityConstant.USER_INFO, "User info is missing");
        }

        if (departmentHierarchyLevel == null) {
            throw new CustomException(DepartmentEntityConstant.INVALID_REQUEST, "Department hierarchy request is invalid");
        }

        if (StringUtils.isBlank(departmentHierarchyLevel.getTenantId())) {
            errorMap.put(DepartmentEntityConstant.TENANT_ID, "Tenant id is missing");
        }
        if (StringUtils.isBlank(departmentHierarchyLevel.getDepartmentId())) {
            errorMap.put(DepartmentEntityConstant.DEPARTMENT_ID, "Department id is missing");
        }
        if (StringUtils.isBlank(departmentHierarchyLevel.getLabel())) {
            errorMap.put(DepartmentEntityConstant.DEPARTMENT_LABEL, "Department label is missing");
        }

        if (StringUtils.isNotBlank(departmentHierarchyLevel.getDepartmentId()) && StringUtils.isNotBlank(departmentHierarchyLevel.getTenantId())) {
            List<String> departments = departmentUtil.getDepartmentFromDepartmentService(departmentHierarchyLevel.getTenantId(),
                    departmentHierarchyLevel.getDepartmentId(), requestHeader);
            if (departments.isEmpty())
                errorMap.put(DepartmentEntityConstant.INVALID_DEPARTMENT_ID, "Department id : " + departmentHierarchyLevel.getDepartmentId()
                        + " doesn't exist in the system");
        }

        if(StringUtils.isNotBlank(departmentHierarchyLevel.getTenantId())
                && StringUtils.isNotBlank(departmentHierarchyLevel.getDepartmentId())
                && StringUtils.isBlank(departmentHierarchyLevel.getParent())) {

            List<DepartmentHierarchyLevel> dbDepartmentHierarchyLevels = levelRepository.searchParentDeptHierarchyLevel(
                    departmentHierarchyLevel.getDepartmentId()
                    , departmentHierarchyLevel.getTenantId()
                    , departmentHierarchyLevel.getParent());
            if (dbDepartmentHierarchyLevels != null && !dbDepartmentHierarchyLevels.isEmpty()) {
                errorMap.put(DepartmentEntityConstant.INVALID_DEPARTMENT_ID, "Department id : " + departmentHierarchyLevel.getDepartmentId()
                        + " is not valid for given parent");

            }
        }

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
    }

    public void validateHierarchyLevelSearchPost(DepartmentHierarchyLevelSearchRequest searchRequest) {
        DepartmentHierarchyLevelSearchCriteria searchCriteria = searchRequest.getCriteria();
        RequestHeader requestHeader = searchRequest.getRequestHeader();
        Map<String, String> errorMap = new HashMap<>();

        //Header validation
        if (requestHeader == null) {
            throw new CustomException(DepartmentEntityConstant.ERROR_REQUEST_HEADER, "Request header is missing");
        }
        if (searchCriteria == null) {
            throw new CustomException(DepartmentEntityConstant.ERROR_SEARCH_CRITERIA, "Search criteria is missing");
        }
        if (StringUtils.isBlank(searchCriteria.getTenantId())) {
            errorMap.put(DepartmentEntityConstant.TENANT_ID, "Government Id (Tenant id) is missing");
        }

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
    }
}
