package org.egov.service;

import org.egov.dto.ErrorDataModel;
import org.egov.model.ErrorDetail;

import java.util.List;

public interface ErrorService {

    void persistError(ErrorDataModel errorDataModel);

    List<ErrorDetail> getAllErrorDetails();
}
