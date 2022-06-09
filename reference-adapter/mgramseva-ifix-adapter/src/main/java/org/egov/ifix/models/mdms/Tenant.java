package org.egov.ifix.models.mdms;
import lombok.*;

import static org.springframework.util.ObjectUtils.isEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tenant {

    private String code;
    private String name;
    private City city;

}