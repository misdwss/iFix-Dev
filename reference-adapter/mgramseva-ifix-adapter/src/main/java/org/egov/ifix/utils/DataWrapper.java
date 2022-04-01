package org.egov.ifix.utils;

import org.egov.ifix.models.ErrorDataModel;
import org.egov.ifix.models.FiscalEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.egov.ifix.utils.EventConstants.*;

@Component
public class DataWrapper {


    /**
     * @param data
     * @param dataName
     * @param attributeName
     * @param attributeValue
     * @param errorType
     * @param status
     * @param message
     * @return
     */
    public Optional<ErrorDataModel> getErrorDataModel(String data, String dataName, String attributeName,
                                                      String attributeValue, String errorType, String status,
                                                      String message) {
        if (!StringUtils.isEmpty(data) && !StringUtils.isEmpty(dataName) && !StringUtils.isEmpty(attributeName)
                && !StringUtils.isEmpty(attributeValue) && !StringUtils.isEmpty(status)) {

            ErrorDataModel errorDataModel = new ErrorDataModel();
            errorDataModel.setData(data);
            errorDataModel.setDataName(dataName);
            errorDataModel.setAttributeName(attributeName);
            errorDataModel.setAttributeValue(attributeValue);
            errorDataModel.setErrorType(errorType);
            errorDataModel.setStatus(status);

            if (!StringUtils.isEmpty(message)) {
                errorDataModel.setMessage(message);
            } else {
                errorDataModel.setMessage(NA);
            }

            errorDataModel.setOrigin(APPLICATION_NAME);
            errorDataModel.setDestination(DESTINATION_APP_NAME);
            errorDataModel.setCreatedBy(APPLICATION_NAME);
            errorDataModel.setCreatedTime(System.currentTimeMillis());

            return Optional.ofNullable(errorDataModel);
        }

        return Optional.empty();
    }

    /**
     * @param fiscalEventList
     * @return
     */
    public String getFiscalEventDetailsInformation(List<FiscalEvent> fiscalEventList) {
        StringBuilder fiscalMessage = new StringBuilder();

        if (fiscalEventList != null && !fiscalEventList.isEmpty()) {
            AtomicInteger index = new AtomicInteger(0);

            fiscalEventList.stream()
                    .forEach(fiscalEvent -> {
                        fiscalMessage.append("[ index " + index);
                        fiscalMessage.append(" Tenant: " + fiscalEvent.getTenantId());
                        fiscalMessage.append(" Project Id: " + fiscalEvent.getProjectId());
                        fiscalMessage.append(" Event Type: " + fiscalEvent.getEventType());
                        fiscalMessage.append(" Amount Details: " + fiscalEvent.getAmountDetails());
                        fiscalMessage.append(" ] ");
                    });
        }

        return fiscalMessage.toString();
    }
}