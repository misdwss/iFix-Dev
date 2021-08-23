package org.egov.ifix.controller;

import javax.validation.Valid;

import org.egov.ifix.models.EventRequest;
import org.egov.ifix.models.EventResponse;
import org.egov.ifix.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class EventController {
	
	@Autowired
	private EventService eventService;
	
	@PostMapping("/events/push")
	public ResponseEntity<?>pushEvents( @Valid @RequestBody EventRequest eventRequest) {
		log.info("request received");
		eventService.pushEvent(eventRequest);
		return new ResponseEntity<>(EventResponse.class, HttpStatus.CREATED);
	}
	
}
