package org.egov.ifix.service;

import org.egov.ifix.models.fiscalEvent.FiscalEvent;

public interface CollectionService {
    void makePspclPaymentByCollectionService(FiscalEvent fiscalEvent, String mgramsevaTenantId);
}
