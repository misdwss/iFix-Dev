package org.egov.repository;

import org.egov.entity.EventPostingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventPostingDetailRepository extends JpaRepository<EventPostingDetail, Long> {

    List<EventPostingDetail> findByStatus(String status);

}