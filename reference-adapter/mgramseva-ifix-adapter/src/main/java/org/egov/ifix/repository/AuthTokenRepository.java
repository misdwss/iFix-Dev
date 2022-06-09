package org.egov.ifix.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.ifix.models.KeyCloackData;
import org.egov.ifix.models.mgramseva.OauthSuccessResponseDTO;
import org.egov.ifix.utils.ApplicationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Repository
@Slf4j
public class AuthTokenRepository {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ApplicationConfiguration applicationConfiguration;


	public KeyCloackData getKeyCloakAuthToken() {

		log.debug("getting token from ifix .........");

		String url = applicationConfiguration.getKeyCloakHost()
				+ applicationConfiguration.getKeyCloakAuthApi();

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
			log.error(e.getMessage(), e);
			throw new RuntimeException("Unable to get authtoken from keycloak");
		}
		log.info("authtoken" + response.getBody().getAccess_token());
		return response.getBody();
	}


	/** TODO: Remove static value
	 * @return
	 */
	public OauthSuccessResponseDTO getMgramsevaOauthAccessToken() {
		String url = applicationConfiguration.getMgramsevaHost()
				+ applicationConfiguration.getMgramsevaOauthAccessTokenURL();;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setAccept(Collections.singletonList(MediaType.ALL));
		headers.setAccessControlAllowOrigin("*");
		headers.setOrigin(applicationConfiguration.getMgramsevaHost());
		headers.setBasicAuth("ZWdvdi11c2VyLWNsaWVudDo=");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("username", "7731045306");
		map.add("password", "@Pple123!");
		map.add("scope", "read");
		map.add("grant_type", "password");
		map.add("tenantId", "pb");
		map.add("userType", "EMPLOYEE");


		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);

		ResponseEntity<OauthSuccessResponseDTO> response = null;
		try {

			response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, OauthSuccessResponseDTO.class);
		} catch (RestClientException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException("Unable to get access token form mgramseva oauth");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException("Unable to get access token form mgramseva oauth");
		}
		log.info("access token" + response.getBody());
		return response.getBody();
	}
}




