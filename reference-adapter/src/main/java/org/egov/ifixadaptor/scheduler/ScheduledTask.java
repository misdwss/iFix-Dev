package org.egov.ifixadaptor.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {
	
	 
	
	@Scheduled (cron="${firetime}")
	public void readAndPush()
	{
		
	}
	
}
