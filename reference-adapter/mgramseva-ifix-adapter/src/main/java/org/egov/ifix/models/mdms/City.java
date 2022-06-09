package org.egov.ifix.models.mdms;

import lombok.*;

import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class City {

    private String name;
    private String code;
    private String projectId;

}