package org.egov.ifix.models.mgramseva;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorCreateRequestDTO {
    @JsonProperty("RequestInfo")
    private RequestInfoDTO requestInfo;
    @JsonProperty("vendor")
    private VendorDTO vendor;
}
