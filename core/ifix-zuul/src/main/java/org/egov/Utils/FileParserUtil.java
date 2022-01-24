package org.egov.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class FileParserUtil {

    @Value("${api.endpoint.role.mapper.filepath}")
    private String apiEndpointRoleMapperFilePath;

    @Autowired
    private ObjectMapper objectMapper;

    public JsonNode readRoleMapping() {
        JsonNode jsonNode = null;
        if (StringUtils.isNotBlank(apiEndpointRoleMapperFilePath)) {
            try {
                jsonNode = objectMapper.readTree(new ClassPathResource(apiEndpointRoleMapperFilePath).getFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonNode;
    }
}
