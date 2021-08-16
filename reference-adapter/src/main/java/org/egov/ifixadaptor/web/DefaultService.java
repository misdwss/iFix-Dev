package org.egov.ifixadaptor.web;

import org.egov.ifixadaptor.model.BadRequestException;
import org.egov.ifixadaptor.model.IEvent;
import org.egov.ifixadaptor.model.IfixEvent;
import org.egov.ifixadaptor.service.AdapterService;
import org.egov.ifixadaptor.service.AdaptorFactory;
import org.egov.ifixadaptor.service.Deduplicator;
import org.egov.ifixadaptor.service.Mapper;
import org.egov.ifixadaptor.service.ModelValidator;
import org.egov.ifixadaptor.service.Transformer;
import org.egov.ifixadaptor.web.contract.Event;
import org.springframework.validation.BindingResult;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DefaultService implements AdapterService {

	@Override
	public void process(String content, BindingResult result, AdaptorFactory factory,String serviceType) {
	
		JsonObject contentJson=null;
		if(!content.isEmpty())
			contentJson = JsonParser.parseString(content).getAsJsonObject();
		else
			throw new BadRequestException(result);
		
		
		
		IEvent event = new Event();
		JsonElement jsonElement = contentJson.get("entities");
		ModelValidator validator = factory.getValidator(((Event)event).getEventType());
		
		
		event = validator.validateBasicContent(contentJson,event,result);
		IEvent entity = factory.getEntity(((Event)event).getEventType(), serviceType);
		
		event = validator.validateEntity(entity,jsonElement);
		event =validator.validatedConditional(event,contentJson);
		
		Deduplicator deduplicator = factory.getDeduplicator(((Event)event).getEventType()) ;
		deduplicator.deDuplicate(event);
		
		
		Transformer transformer = factory.getTransformer(((Event)event).getEventType());
		IfixEvent ifixEvent = transformer.transform(event);
		Mapper mapper=factory.getMapper(((Event)event).getEventType());
		mapper.map(event,ifixEvent);
		
		
		
		
		 
		
	}

}
