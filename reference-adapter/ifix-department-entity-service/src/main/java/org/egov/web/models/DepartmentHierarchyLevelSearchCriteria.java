package org.egov.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * The object contains all the search criteria of Department Hierarchy Level
 */
@ApiModel(description = "The object contains all the search criteria of Department Hierarchy Level")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2021-08-23T11:51:49.710+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentHierarchyLevelSearchCriteria {
    @JsonProperty("Ids")
    @Valid
    private List<String> ids = null;

    @JsonProperty("tenantId")
    private String tenantId = null;

    @JsonProperty("departmentId")
    private String departmentId = null;

    @JsonProperty("label")
    private String label = null;

    @JsonProperty("level")
    private Integer level = null;


    public DepartmentHierarchyLevelSearchCriteria addIdsItem(String idsItem) {
        if (this.ids == null) {
            this.ids = new ArrayList<>();
        }
        this.ids.add(idsItem);
        return this;
    }

}

