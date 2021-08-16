package org.egov.ifixadaptor.web;

import org.egov.ifixadaptor.model.BadRequestException;
import org.egov.ifixadaptor.model.EventResponse;
import org.egov.ifixadaptor.service.AdaptorFactory;
import org.egov.ifixadaptor.web.contract.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class TransactionController {

	@Autowired
	SmartValidator validator;
	
	@Autowired
	ApplicationContext context;
	
	@Value("${facrory.class}")
	private String factoryName;
	

	@PostMapping("/event/v1/_push")
	@ResponseBody
	public ResponseEntity<?> transaction(@RequestBody String content, BindingResult result) {

	    AdaptorFactory factory =(AdaptorFactory) context.getBean(factoryName);
	    factory.getService().process(content, result, factory,"rest");

		EventResponse response = new EventResponse();

		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}


}
