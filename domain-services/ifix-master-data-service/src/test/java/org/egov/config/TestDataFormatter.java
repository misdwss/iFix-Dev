package org.egov.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.web.models.GovernmentRequest;
import org.egov.web.models.GovernmentResponse;
import org.egov.web.models.GovernmentSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class TestDataFormatter {
    @Autowired
    private TestProperties testProperties;

    /**
     * @param fileName
     * @return
     */
    private File getFileFromClassLoaderResource(String fileName) {
        String basePackage = testProperties.getTestDataPackage();
        ClassLoader classLoader = this.getClass().getClassLoader();

        if (basePackage != null && !basePackage.isEmpty()) {
            String baseURL = classLoader.getResource(basePackage).getFile();
            fileName = baseURL + File.separator + fileName;

            return new File(fileName);
        }else {
            return new File(classLoader.getResource(fileName).getFile());
        }
    }


    /**
     * @return
     */
    public GovernmentRequest getGovernmentRequestData() throws IOException {
        GovernmentRequest governmentRequest = new ObjectMapper()
                .readValue(getFileFromClassLoaderResource(testProperties.getGovCreateRequestData()),
                        GovernmentRequest.class);

        return governmentRequest;
    }

    /**
     * @return
     * @throws IOException
     */
    public GovernmentResponse getGovernmentCreateResponseData() throws IOException {
        GovernmentResponse governmentResponse = new ObjectMapper()
                .readValue(getFileFromClassLoaderResource(testProperties.getGovCreateResponseData()),
                        GovernmentResponse.class);

        return governmentResponse;
    }

    /**
     * @return
     * @throws IOException
     */
    public GovernmentSearchRequest getGovernmentSearchRequestData() throws IOException {
        GovernmentSearchRequest governmentSearchRequest = new ObjectMapper()
                .readValue(getFileFromClassLoaderResource(testProperties.getGovSearchRequestData()),
                        GovernmentSearchRequest.class);

        return governmentSearchRequest;
    }

    /**
     * @return
     * @throws IOException
     */
    public GovernmentResponse getGovernmentSearchResponseData() throws IOException {
        GovernmentResponse governmentResponse = new ObjectMapper()
                .readValue(getFileFromClassLoaderResource(testProperties.getGovSearchResponseData()),
                        GovernmentResponse.class);

        return governmentResponse;
    }

}
