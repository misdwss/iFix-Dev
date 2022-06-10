package org.egov.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.model.KeyCloackData;
import org.egov.repository.AuthTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenService {

    @Autowired
    private AuthTokenRepository authTokenRepository;

    public String getAuthToken() {
        KeyCloackData data = authTokenRepository.getAuthToken();
        return data.getAccess_token();
    }
}
