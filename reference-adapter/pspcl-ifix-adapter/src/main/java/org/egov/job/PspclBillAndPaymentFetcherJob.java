package org.egov.job;

import client.stub.GetBillResult;
import client.stub.GetPaymentResult;
import lombok.extern.slf4j.Slf4j;
import org.egov.model.AccountNumberGpMappingVO;
import org.egov.model.ReconcileVO;
import org.egov.service.PspclBillAndPaymentReconcileService;
import org.egov.util.MDMSClient;
import org.egov.util.PspclUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class PspclBillAndPaymentFetcherJob extends QuartzJobBean {

    @Autowired
    private PspclUtil pspclUtil;

    @Autowired
    private PspclBillAndPaymentReconcileService pspclBillAndPaymentReconcileService;

    @Autowired
    private MDMSClient mdmsClient;


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("Job ** {} ** fired @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());
        List<ReconcileVO> reconcileVOS = new ArrayList<>();

        try {
            List<AccountNumberGpMappingVO> acnGpMappingVOs = mdmsClient.getAcnGpMappingVOs();
            if (!acnGpMappingVOs.isEmpty()) {
                for (AccountNumberGpMappingVO acnGpMappingVO : acnGpMappingVOs) {
                    //get the bills And payments from PSPCL system
                    List<GetBillResult> pspclBillResults = pspclUtil.getBillsFromPspcl(acnGpMappingVO.getAccountNumber());
                    List<GetPaymentResult> pspclPaymentResults = pspclUtil.getPaymentsFromPspcl(acnGpMappingVO.getAccountNumber());

                    ReconcileVO reconcileVO = pspclBillAndPaymentReconcileService.reconcile(pspclBillResults, pspclPaymentResults);

                    if (reconcileVO != null && !reconcileVO.isStatus()) {
                        reconcileVO.setDepartmentEntityCode(acnGpMappingVO.getDepartmentEntityCode());
                        reconcileVO.setDepartmentEntityName(acnGpMappingVO.getDepartmentEntityName());

                        reconcileVOS.add(reconcileVO);
                    }

                }

            }

            pspclBillAndPaymentReconcileService.publishFiscalEvent(reconcileVOS);

        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
        log.info("Next job scheduled @ {}", context.getNextFireTime());
    }
}
