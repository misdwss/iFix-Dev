package org.egov.ifixadaptor.service;

import org.springframework.validation.BindingResult;

public interface AdapterService {
	void process(String content, BindingResult result, AdaptorFactory factory,String serviceType);
	
	

}
