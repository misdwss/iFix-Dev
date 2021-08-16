package org.egov.ifixadaptor.service;

import org.egov.ifixadaptor.model.IEvent;

public interface Deduplicator {

	void deDuplicate(IEvent event);

}
