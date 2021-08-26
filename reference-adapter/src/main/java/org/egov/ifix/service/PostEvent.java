package org.egov.ifix.service;

import org.egov.ifix.models.EventResponse;
import org.egov.ifix.models.FiscalEventRequest;
import org.egov.ifix.models.FiscalEventResponse;
import org.egov.ifix.models.KeyCloackData;
import org.egov.ifix.utils.ApplicationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostEvent {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ApplicationConfiguration applicationConfiguration;

	// when redis cache implemented this will be removed
	private String token = null;

	public FiscalEventResponse post(FiscalEventRequest event) {

		token = getAuthToken();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(token);
		ResponseEntity<FiscalEventResponse> response =null; 
		
		HttpEntity<FiscalEventRequest> request = new HttpEntity<>(event, headers);
		
		String url = applicationConfiguration.getIfixHost() + applicationConfiguration.getIfixEventEndPoint();

		try {
			   response = restTemplate.postForEntity(url, request, FiscalEventResponse.class);
		} catch (RestClientException e) {
			e.printStackTrace();
			
			
		}
		log.info("status" +response.getStatusCode());

		return response.getBody();
	}

	/**
	 * 
	 * @return token Get the keyCloak Bearer token from redis if expired refresh
	 *         the token
	 */
	private String getAuthToken() {

		if (token == null) {
			String url = applicationConfiguration.getKeyCloakHost()
					+ applicationConfiguration.getKeyCloakAuthEndPoint();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("client_id", applicationConfiguration.getClientId());
			map.add("client_secret", applicationConfiguration.getClientSecret());
			map.add("grant_type", applicationConfiguration.getGrantType());

			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

			ResponseEntity<KeyCloackData> response = null;
			try {

				response = restTemplate.exchange(url, HttpMethod.POST, entity, KeyCloackData.class);
			} catch (RestClientException e) {
				log.error("Unable to get authtoken from keycloak");
				throw new RuntimeException("Unable to get authtoken from keycloak");
			}
			if (response != null) {
				log.info("statuss  " + response.getStatusCode() + "  token and other details " + response.getBody());
				token = response.getBody().getAccess_token();
				// store the expires and then check evertime to find out to call
				// refresh api or not
			}

		}
		return token;

	}

}
