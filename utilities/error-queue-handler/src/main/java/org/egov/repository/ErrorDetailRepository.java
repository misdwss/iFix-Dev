package org.egov.repository;

import org.egov.model.ErrorDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorDetailRepository extends JpaRepository<ErrorDetail, Long> {

}
