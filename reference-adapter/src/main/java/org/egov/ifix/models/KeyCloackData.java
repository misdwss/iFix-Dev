package org.egov.ifix.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyCloackData {
	
	public String client_id;
	public String client_secret;
	public String grant_type;
	public String access_token;
	public Long expires_in;
	public Long refresh_expires_in;
	public String token_type;
	public String scope;

}
