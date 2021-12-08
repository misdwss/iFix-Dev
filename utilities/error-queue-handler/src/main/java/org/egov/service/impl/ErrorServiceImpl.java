package org.egov.service.impl;

import org.egov.dto.ErrorDataModel;
import org.egov.model.ErrorDetail;
import org.egov.repository.ErrorDetailRepository;
import org.egov.service.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ErrorServiceImpl implements ErrorService {

    @Autowired
    private ErrorDetailRepository errorDetailRepository;

    /**
     * @param errorDataModel
     */
    public void persistError(ErrorDataModel errorDataModel) {
        if (errorDataModel != null) {
            ErrorDetail errorDetail = new ErrorDetail();

            errorDetail.setData(errorDataModel.getData());
            errorDetail.setDataName(errorDataModel.getDataName());
            errorDetail.setAttributeName(errorDataModel.getAttributeName());
            errorDetail.setAttributeValue(errorDataModel.getAttributeValue());
            errorDetail.setErrorType(errorDataModel.getErrorType());
            errorDetail.setOrigin(errorDataModel.getOrigin());
            errorDetail.setDestination(errorDataModel.getDestination());
            errorDetail.setStatus(errorDataModel.getStatus());
            errorDetail.setMessage(errorDataModel.getMessage());
            errorDetail.setEruptionTime(errorDataModel.getCreatedTime());
            errorDetail.setCreatedDate(new Date());
            errorDetail.setCreatedBy(errorDataModel.getCreatedBy());


            errorDetailRepository.save(errorDetail);
        }
    }

    public List<ErrorDetail> getAllErrorDetails() {
        return errorDetailRepository.findAll();
    }
}
