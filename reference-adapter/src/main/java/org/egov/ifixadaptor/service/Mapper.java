package org.egov.ifixadaptor.service;

import org.egov.ifixadaptor.model.IEvent;
import org.egov.ifixadaptor.model.IfixEvent;

public interface Mapper {

	IfixEvent map(IEvent event, IfixEvent ifixEvent);

}
