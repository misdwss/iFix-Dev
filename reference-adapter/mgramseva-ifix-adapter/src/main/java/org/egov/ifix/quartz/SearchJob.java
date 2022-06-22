package org.egov.ifix.quartz;

import lombok.extern.slf4j.Slf4j;
import org.egov.ifix.service.PspclEventService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class SearchJob extends QuartzJobBean {
    @Autowired
    private PspclEventService pspclEventService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>> JOB started at " + new Date(System.currentTimeMillis()) + " <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"); //TODO: Will be removed by actual call

//            pspclEventService.pushPspclEventToMgramseva("Demand");
//            pspclEventService.pushPspclEventToMgramseva("Receipt");

        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
