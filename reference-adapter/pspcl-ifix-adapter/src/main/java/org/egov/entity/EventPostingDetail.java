package org.egov.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "pspcl_event_posting_detail")
@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventPostingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 64)
    private String tenantId;

    @Size(max = 64)
    private String ifixEventId;

    @Size(max = 64)
    private String referenceId;

    @Size(max = 16)
    private String eventType;

    @Size(max = 16)
    private String status;

    @Size(max = 4000, min = 0)
    private String error;

    @Lob
    private String record;

    private Date createdDate;

    private Date lastModifiedDate;
}
