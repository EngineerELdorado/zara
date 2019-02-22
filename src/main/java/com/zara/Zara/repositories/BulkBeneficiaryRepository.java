package com.zara.Zara.repositories;

import com.zara.Zara.entities.BulkBeneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface BulkBeneficiaryRepository extends JpaRepository<BulkBeneficiary, Long> {

    @Query(value = "select * from bulk_beneficiaries where business_id=?1", nativeQuery = true)
    Collection<BulkBeneficiary> findByBusinessId(Long id);
}
