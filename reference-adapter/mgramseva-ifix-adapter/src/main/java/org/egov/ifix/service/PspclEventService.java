package org.egov.ifix.service;

import org.egov.ifix.models.mgramseva.MgramsevaChallanResponseDTO;

import java.util.Optional;

public interface PspclEventService {
    void pushPspclEventToMgramseva(String eventType);

    Optional<MgramsevaChallanResponseDTO> getOldestChallanByBillDate(String tenantId);
}
