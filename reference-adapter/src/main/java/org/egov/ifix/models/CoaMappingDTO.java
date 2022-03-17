package org.egov.ifix.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoaMappingDTO {
    private String clientCode;
    private String iFixCoaCode;
}
