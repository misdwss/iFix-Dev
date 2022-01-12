package org.egov.ifix.service;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.egov.ifix.cache.AdapterCache;
import org.egov.ifix.exception.HttpCustomException;
import org.egov.ifix.models.ChartOfAccount;
import org.egov.ifix.models.ErrorDataModel;
import org.egov.ifix.repository.ChartOfAccountRepository;
import org.egov.ifix.utils.ApplicationConfiguration;
import org.egov.ifix.utils.DataWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.egov.ifix.utils.EventConstants.*;

/**
 * @author mani
 * This will manage getting the right chartofaccount in running
 * environment. This will cache the data once received from repository.
 * Expiry is not set it will be global config which will clear cache on
 * specified time It depends on repository to provide COA object for
 * given code This will include validation whether the adapter is
 * autherised to post chartofaccount to IFIX Validated coas put to cache
 * invalid are logged
 */
@Service
@Slf4j
public class ChartOfAccountService {

    @Autowired
    private ChartOfAccountRepository chartOfAccountRepository;

    @Autowired
    private AdapterCache<ChartOfAccount> coaCache;

    @Autowired
    private DataWrapper dataWrapper;

    @Autowired
    private ApplicationConfiguration applicationConfiguration;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * @param clientCoaCode
     * @param jsonObject
     * @return
     */
    public String getChartOfAccount(String clientCoaCode, JsonObject jsonObject) {
        ChartOfAccount coa = coaCache.getValue(clientCoaCode);

        if (coa == null) {
            Optional<ChartOfAccount> coaOptional = chartOfAccountRepository.getChartOfAccount(clientCoaCode, jsonObject);

            if (coaOptional.isPresent()) {
                coaCache.putValue(clientCoaCode, coaOptional.get());
            } else {
                Optional<ErrorDataModel> errorDataModelOptional = dataWrapper.getErrorDataModel(NA,
                        COA_CODE_DATA_NAME, CLIENT_COA_CODE, clientCoaCode, NON_RECOVERABLE_ERROR,
                        HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                        "Unable to receive iFix coa corresponding to mgramseva coa code");

                if (errorDataModelOptional.isPresent()) {
                    kafkaTemplate.send(applicationConfiguration.getErrorTopicName(), errorDataModelOptional.get());
                }
                throw new HttpCustomException(COA_CODE, "Unable to find COA by client coa code", HttpStatus.BAD_REQUEST);
            }
        } else {
            log.info(LOG_INFO_PREFIX + "got coa from Cache" + coa);
        }
        return coa.getId();
    }
}
