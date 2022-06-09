package org.egov.ifix.models.mgramseva;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MgramsevaCalculationResponseDTO {
    @JsonProperty("challanNo")
    private Object challanNo;
    @JsonProperty("challan")
    private Object challan;
    @JsonProperty("tenantId")
    private String tenantId;
    @JsonProperty("taxHeadEstimates")
    private List<MgramsevaTaxHeadEstimateResponseDTO> taxHeadEstimates = null;
}
