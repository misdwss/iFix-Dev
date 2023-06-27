package org.egov.service;

import client.stub.BillResultData;
import client.stub.GetBillResult;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.entity.PspclBillDetail;
import org.egov.mapper.PspclDataEntityMapper;
import org.egov.model.ReconcileVO;
import org.egov.repository.PspclBillDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class PspclBillReconcileService {

    @Autowired
    private PspclDataEntityMapper pspclDataEntityMapper;

    @Autowired
    private PspclBillDetailRepository billDetailRepository;


    public void reconcileBill(List<BillResultData> pspclBillResults, ReconcileVO reconcileVO) {
        if (pspclBillResults != null && !pspclBillResults.isEmpty()) {
            BillResultData currentMonthBillResult = pspclBillResults.get(0);
// chaged this as orderByIsNull now
            //we weree checking duplicate before usinnng orderByColumn nnow we are usinng billNumber column
            List<PspclBillDetail> pspclBillDetails = billDetailRepository
                    .findByBillNumberAndAccountNumber(currentMonthBillResult.getBillNumber(), currentMonthBillResult.getAccountNumber());
            if (pspclBillDetails != null && !pspclBillDetails.isEmpty()) {
                reconcileVO.setBillReconcile(true);
            } else {
                PspclBillDetail currentPspclBillDetail = pspclDataEntityMapper.mapPspclBillToEntity(currentMonthBillResult);
                reconcileVO.setCurrentPspclBillDetail(currentPspclBillDetail);
            }
        }
    }


    public int  getBillByAccountNumber(String accountNumber) {
        if(!StringUtils.isEmpty(accountNumber) && !accountNumber.isEmpty()) {
            List<PspclBillDetail> pspclBillDetails = billDetailRepository
                    .findByAccountNumber(accountNumber);
            if (pspclBillDetails !=null && !pspclBillDetails.isEmpty()) {
                return pspclBillDetails.size();
            }else {
                return 0;
            }
        }
        return 0;
    }
}