package org.egov.ifixadaptor.service;

import org.egov.ifixadaptor.model.BadRequestException;
import org.egov.ifixadaptor.model.IEvent;
import org.egov.ifixadaptor.web.contract.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DefaultValidator implements ModelValidator {
	
	@Autowired
	SmartValidator validator;
	
	
	public IEvent validateBasicContent(JsonObject contentJson, IEvent event, BindingResult result) {
		if(contentJson!=null)
		{
			try {
				if(contentJson.get("eventType")!=null)
					((Event)event).setEventType(contentJson.get("eventType").getAsString());
				if(contentJson.get("id")!=null)
					((Event)event).setId(contentJson.get("id").getAsString());
				if(contentJson.get("tenantId")!=null)
					((Event)event).setTenantId(contentJson.get("tenantId").getAsString());
				validator.validate(((Event)event), result);


			} catch (Exception e) {

				throw new BadRequestException(result);

			}
		}
		 
		return event;

	}


	


	@Override
	public IEvent validateEntity(IEvent entity, JsonElement entitiesElement) {
		
		return null;
	}


	 





	




	@Override
	public IEvent validatedConditional(IEvent event, JsonElement entity) {
		// TODO Auto-generated method stub
		return null;
	}

}
