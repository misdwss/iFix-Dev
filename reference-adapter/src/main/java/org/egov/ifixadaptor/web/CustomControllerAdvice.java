package org.egov.ifixadaptor.web;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.response.Error;
import org.egov.common.contract.response.ErrorField;
import org.egov.common.contract.response.ErrorResponse;
import org.egov.ifixadaptor.model.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class CustomControllerAdvice {

	private static final Logger LOG = LoggerFactory.getLogger(CustomControllerAdvice.class);

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public String handleMissingParamsError(Exception ex) {
		return ex.getMessage();
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BadRequestException.class)
	@ResponseBody
	public ResponseEntity<?>  handleValidationExceptions(
			BadRequestException ex) {
	   // Map<String, String> errors = new HashMap<>();
		Error error=new Error();
		error.setCode(400);
		error.setMessage("Missing fields");
		error.setDescription("Add/correct erors");
	    List<ErrorField> errors=new ArrayList<>();
	  
	    ErrorResponse errorResponse = new ErrorResponse();
	    ex.getResult().getAllErrors().forEach((errorr) -> {
	        String fieldName = ((FieldError) errorr).getField();
	        String errorMessage = errorr.getDefaultMessage();
	        ErrorField fieldError=new ErrorField();
	        fieldError.setField(fieldName);
	        fieldError.setMessage(errorMessage);
	        errors.add(fieldError);
	        
	    });
	    error.setFields(errors);
	    errorResponse.setError(error);
	   	errorResponse.setResponseInfo(null);
		 

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

}