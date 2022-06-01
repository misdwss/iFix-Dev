package org.egov.repository;

import org.egov.entity.PspclBillDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface PspclBillDetailRepository extends JpaRepository<PspclBillDetail, Long> {

    @Query(value = "SELECT * FROM pspcl_bill_detail WHERE BILL_ISSUE_DATE = ?1", nativeQuery = true)
    Optional<PspclBillDetail> findByBILL_ISSUE_DATE(Date date_reading_prev);
}
