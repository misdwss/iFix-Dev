package org.egov.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.egov.common.contract.response.ResponseHeader;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the ResponseHeader and the enriched Expenditure information
 */
@ApiModel(description = "Contains the ResponseHeader and the enriched Expenditure information")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2021-08-02T16:24:12.742+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class ExpenditureResponse {
    @JsonProperty("responseHeader")
    private ResponseHeader responseHeader = null;

    @JsonProperty("expenditure")
    @Valid
    private List<Expenditure> expenditure = null;


    public ExpenditureResponse addEatItem(Expenditure expenditureItem) {
        if (this.expenditure == null) {
            this.expenditure = new ArrayList<>();
        }
        this.expenditure.add(expenditureItem);
        return this;
    }

}

