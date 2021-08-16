package org.egov.ifixadaptor.service;

import org.egov.ifixadaptor.model.IEvent;
import org.egov.ifixadaptor.web.contract.Event;
import org.springframework.validation.BindingResult;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface ModelValidator {
	IEvent validateBasicContent(JsonObject contentJson, IEvent event, BindingResult result);
	IEvent validateEntity(IEvent entity,JsonElement entitiesElement);
	IEvent validatedConditional(IEvent event, JsonElement entity);

}
