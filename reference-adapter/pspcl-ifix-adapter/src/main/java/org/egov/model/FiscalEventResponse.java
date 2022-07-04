package org.egov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseHeader;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the ResponseHeader and the enriched fiscal information
 */
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2021-08-09T17:28:42.515+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FiscalEventResponse {

    @JsonProperty("responseHeader")
    private ResponseHeader responseHeader = null;

    @JsonProperty("fiscalEvent")
    @Valid
    private List<FiscalEvent> fiscalEvent = null;


    public FiscalEventResponse addFiscalEventItem(FiscalEvent fiscalEventItem) {
        if (this.fiscalEvent == null) {
            this.fiscalEvent = new ArrayList<>();
        }
        this.fiscalEvent.add(fiscalEventItem);
        return this;
    }

}
