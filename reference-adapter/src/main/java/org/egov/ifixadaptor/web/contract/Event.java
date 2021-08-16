package org.egov.ifixadaptor.web.contract;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.egov.ifixadaptor.model.IEvent;  

public class Event implements IEvent {
	@NotEmpty
	@Size(min=12,max=128)
	String id;

	@NotEmpty
	@Size(min=4,max=128)
	String tenantId;

	@NotEmpty
	@Size(min=4,max=64)
	String eventType;
	
//	@Size(min=1,max=100)
    List<IEvent> entities;
	
	
	public String getId() {
		return id;
	}
	public List<IEvent> getEntities() {
		return entities;
	}
	public void setEntities(List<IEvent> entities) {
		this.entities = entities;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	    

}   
