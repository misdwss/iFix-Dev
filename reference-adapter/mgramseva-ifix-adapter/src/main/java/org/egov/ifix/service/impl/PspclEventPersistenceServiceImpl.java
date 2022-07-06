package org.egov.ifix.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.ifix.persistance.PspclEventDetail;
import org.egov.ifix.persistance.PspclEventDetailRepository;
import org.egov.ifix.service.PspclEventPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class PspclEventPersistenceServiceImpl implements PspclEventPersistenceService {

    @Autowired
    private PspclEventDetailRepository pspclEventDetailRepository;


    public PspclEventDetail saveFailedPspclEventDetail(String mgramsevaTenantId, String eventType,
                                                        String data, String error) {
        return savePspclEventDetail(false, mgramsevaTenantId, eventType, "N/A", data, error);
    }

    /**
     * @param mgramsevaTenantId
     * @param eventType
     * @return
     */
    public PspclEventDetail saveSuccessPspclEventDetail(String mgramsevaTenantId, String eventType, String eventId) {
        return savePspclEventDetail(true, mgramsevaTenantId, eventType, eventId, null, null);
    }

    /**
     * @param isSuccess
     * @param mgramsevaTenantId
     * @param eventType
     * @param data
     * @param error
     * @return
     */
    private PspclEventDetail savePspclEventDetail(boolean isSuccess, String mgramsevaTenantId, String eventType,
                                                    String eventId, String data, String error) {
        PspclEventDetail pspclEventDetail = new PspclEventDetail();
        pspclEventDetail.setEventType(eventType);
        pspclEventDetail.setTenantId(mgramsevaTenantId);
        pspclEventDetail.setCreatedDate(new Date());
        pspclEventDetail.setSuccess(isSuccess);

        if (!isSuccess) {
            pspclEventDetail.setError(error);
            pspclEventDetail.setData(data);
        }

        return pspclEventDetailRepository.save(pspclEventDetail);
    }
}
