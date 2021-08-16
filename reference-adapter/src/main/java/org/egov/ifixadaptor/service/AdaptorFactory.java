package org.egov.ifixadaptor.service;

import org.egov.ifixadaptor.model.IEvent;

public interface AdaptorFactory {
	
	IEvent getEntity(String name,String serviceType);

	ModelValidator   getValidator(String name);
	
	Deduplicator  getDeduplicator(String name);
	
	Transformer getTransformer(String name);
	
	Mapper getMapper(String name);
	
	Anonymizer getAnonymizer(String name);
	
	AdapterService getService();
	
	
	

}
