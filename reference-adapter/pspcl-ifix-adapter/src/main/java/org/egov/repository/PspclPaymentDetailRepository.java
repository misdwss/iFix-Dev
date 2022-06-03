package org.egov.repository;

import org.egov.entity.PspclPaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PspclPaymentDetailRepository extends JpaRepository<PspclPaymentDetail, Long> {
    List<PspclPaymentDetail> findByTXNDATEBetween(Date fromDate, Date toDate);

    List<PspclPaymentDetail> findByTXNDATEBetweenOrderByTXNDATEDesc(Date fromDate, Date toDate);

    Optional<PspclPaymentDetail> findByTXNID(String txnId);
}
