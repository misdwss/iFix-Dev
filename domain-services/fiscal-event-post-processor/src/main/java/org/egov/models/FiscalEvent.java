package org.egov.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.egov.common.contract.AuditDetails;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * This object captures the fiscal information of external systems.
 */
@ApiModel(description = "This object captures the fiscal information of external systems.")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2021-08-09T17:28:42.515+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FiscalEvent {

    @JsonProperty("id")
    private String id = null;

    @JsonProperty("tenantId")
    private String tenantId = null;

    @JsonProperty("eventType")
    private EventTypeEnum eventType = null;

    @JsonProperty("ingestionTime")
    private Long ingestionTime = null;

    @JsonProperty("eventTime")
    private Long eventTime = null;

    @JsonProperty("referenceId")
    private String referenceId = null;

    @JsonProperty("linkedEventId")
    private String linkedEventId = null;

    @JsonProperty("linkedReferenceId")
    private String linkedReferenceId = null;

    @JsonProperty("amountDetails")
    @Valid
    private List<Amount> amountDetails = new ArrayList<>();

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails = null;

    @JsonProperty("attributes")
    private Object attributes = null;


    public enum EventTypeEnum {
        Sanction("Sanction"),
        Appropriation("Appropriation"),
        Allocation("Allocation"),
        Intra_Transfer("Intra_Transfer"),
        Inter_Transfer("Inter_Transfer"),
        Demand("Demand"),
        Receipt("Receipt"),
        Bill("Bill"),
        Payment("Payment");

        private String value;

        EventTypeEnum(String value) {
            this.value = value;
        }

        @JsonCreator
        public static EventTypeEnum fromValue(String text) {
            for (EventTypeEnum eventTypeEnum : EventTypeEnum.values()) {
                if (String.valueOf(eventTypeEnum.value).equals(text)) {
                    return eventTypeEnum;
                }
            }
            return null;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }
    }

    public FiscalEvent addAmountDetailsItem(Amount amountDetailsItem) {
        this.amountDetails.add(amountDetailsItem);
        return this;
    }
}
