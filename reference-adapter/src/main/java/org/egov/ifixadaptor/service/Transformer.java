package org.egov.ifixadaptor.service;

import org.egov.ifixadaptor.model.IEvent;
import org.egov.ifixadaptor.model.IfixEvent;

public interface Transformer {

	IfixEvent transform(IEvent event);

}
