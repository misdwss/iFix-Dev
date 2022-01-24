package org.egov.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.egov.contract.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Component
public class JwtUtil {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileParserUtil fileParserUtil;

    private static final String API_ENDPOINT_KEY = "api-endpoint";
    private static final String API_ROLE_KEY = "role-list";

    public UserInfo createUserInfoFromJwt(String jwtPayload) throws JsonProcessingException {
        JsonNode payload = objectMapper.readTree(jwtPayload);
        UserInfo userInfo = UserInfo.builder().build();

        userInfo.setUuid(payload.get("sub").asText());

        ArrayNode arrayNode = (ArrayNode) payload.get("realm_access").get("roles");
        List<String> roles = Arrays.asList(objectMapper.readValue(arrayNode.toString(), String[].class));
        userInfo.setRoles(roles);

        List<String> tenants = new ArrayList<>();
        tenants.add(payload.get("tenantId").asText());
        userInfo.setTenants(tenants);

        return userInfo;
    }

    public boolean verifyRoleFromJwt(String requestUri, String jwtPayload) throws JsonProcessingException {
        JsonNode payload = objectMapper.readTree(jwtPayload);
        ArrayNode arrayNode = (ArrayNode) payload.get("realm_access").get("roles");
        List<String> roles = Arrays.asList(objectMapper.readValue(arrayNode.toString(), String[].class));

        String requestPath = requestUri.substring(requestUri.indexOf('/', 1));
        JsonNode fileJsonNode = fileParserUtil.readRoleMapping();
        if (fileJsonNode != null && !fileJsonNode.isEmpty()) {
            //check the request path mapping is there or not
            List<JsonNode> endPointList = fileJsonNode.findValues(API_ENDPOINT_KEY);
            if (endPointList != null && !endPointList.isEmpty()) {
                boolean isPresent = endPointList.stream().anyMatch(endPoint -> endPoint.asText().equals(requestPath));
                if (!isPresent) {
                    return false;
                }
            }
            //authorize - check designated role is present for the endpoint
            Iterator<JsonNode> jsonNodeIterator = fileJsonNode.iterator();
            while (jsonNodeIterator != null && jsonNodeIterator.hasNext()) {
                JsonNode tmpNode = jsonNodeIterator.next();

                if (tmpNode != null && !tmpNode.isEmpty()) {
                    List<JsonNode> tmpEndPointList = tmpNode.findValues(API_ENDPOINT_KEY);

                    if (tmpEndPointList != null && !tmpEndPointList.isEmpty()) {
                        if (tmpEndPointList.stream().anyMatch(endPoint -> endPoint.asText().equals(requestPath))) {
                            Iterator<JsonNode> nodeIterator = tmpNode.iterator();

                            while (nodeIterator != null && nodeIterator.hasNext()) {
                                JsonNode node = nodeIterator.next();

                                if (node != null
                                    && !node.isEmpty()
                                    && node.findValue(API_ENDPOINT_KEY).asText().equals(requestPath)) {

                                    String roleList = node.findParent(API_ENDPOINT_KEY).findValue(API_ROLE_KEY).asText();
                                    String[] roleArr = roleList.split(",");
                                    for (String role : roleArr) {
                                        if (roles.contains(role)) {
                                            return true;
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }

        }
        return false;
    }
}
