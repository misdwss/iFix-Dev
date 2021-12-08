package org.egov.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ErrorDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	
//	@Lob
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private String data;

	private String attributeName;

	private String attributeValue;

	private String dataName;

	private String errorType;

	private String status;

	private String message;

	private String origin;

	private String destination;

	private Long eruptionTime;

	private String createdBy;

	private String modifiedBy;

	private Date createdDate;

	private Date lastModifiedDate;


}
