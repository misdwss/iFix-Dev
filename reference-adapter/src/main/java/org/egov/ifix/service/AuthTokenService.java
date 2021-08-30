package org.egov.ifix.service;

import java.util.concurrent.TimeUnit;

import org.egov.ifix.cache.AdapterCache;
import org.egov.ifix.models.KeyCloackData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthTokenService {
	
	private static final String AUTHTOKEN = "authToken";

	@Autowired
	private AuthTokenRepository authTokenRepository;
	
	@Autowired
	private AdapterCache<KeyCloackData> adapterCache; 
	
	private static final  Long TIMEBEFOREEXPIRY=5l*60*1000;
	
	
	
	
	public String getAuthToken()
	{
		
		KeyCloackData data = adapterCache.getValue(AUTHTOKEN);
		if(data==null)
		{
		 data = authTokenRepository.getAuthToken();
		 if(data.getExpires_in()>TIMEBEFOREEXPIRY)		 
			 adapterCache.putValueWithExpireTime(AUTHTOKEN, data,data.getExpires_in()-TIMEBEFOREEXPIRY,TimeUnit.MILLISECONDS);
		 else
			 adapterCache.putValueWithExpireTime(AUTHTOKEN, data,data.getExpires_in(),TimeUnit.MILLISECONDS);	 
		} else
		{
			log.info("got token from Cache",data.getAccess_token());
		}
		
		return data.getAccess_token();
		
		

	
	}

}
