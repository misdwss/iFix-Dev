package org.egov.ifixadaptor.model;

import org.springframework.validation.BindingResult;
/**
 * 
 * @author mani
 *
 */
public class BadRequestException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private BindingResult result;

	public BadRequestException(BindingResult result) {
		super();
		this.result = result;
	}

	public BindingResult getResult() {
		return result;
	}

	public void setResult(BindingResult result) {
		this.result = result;
	}

}
