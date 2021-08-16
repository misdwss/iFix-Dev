package org.egov.ifixadaptor.service;

import java.util.Map;
import java.util.Set;

import org.egov.ifixadaptor.model.IDemand;
import org.egov.ifixadaptor.model.IEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class DefaultFactory implements AdaptorFactory {
	
	@Autowired
	ApplicationContext context;
	
	
	public IEvent  getEntity(String name,String serviceType)
	{
		if(serviceType==null)
			serviceType="rest";
		Class typeName=null;
		IEvent entity=null;
		
		if(name.equalsIgnoreCase("Demand"))
			typeName=IDemand.class;
		 
		
		
		Map<String, IEvent> beansOfType = context.getBeansOfType(typeName);
		 
			
		if(beansOfType.size()==1)
			entity= (IEvent) beansOfType.get(0);
		else if(beansOfType.size()==2)
		{
			Set<Map.Entry<String, IEvent>> entries = beansOfType.entrySet();

			for (Map.Entry<String, IEvent> entry : entries) {
			if(entry.getValue().getClass().getCanonicalName().contains("client"))
			entity=entry.getValue();
			}
		}
		else
			throw new RuntimeException("More than 2 Classes found for "+typeName);
		
		return entity;
	  
	}


	@Override
	public ModelValidator getValidator(String name) {
		return null;
	}


	@Override
	public Deduplicator getDeduplicator(String name) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Transformer getTransformer(String name) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Mapper getMapper(String name) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Anonymizer getAnonymizer(String name) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public AdapterService getService() {
		// TODO Auto-generated method stub
		return null;
	}


	 


		
	
		
		
		
	

}
