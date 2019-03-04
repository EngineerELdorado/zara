package com.zara.Zara.repositories;

import com.zara.Zara.entities.BulkBeneficiary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface BulkBeneficiaryRepository extends JpaRepository<BulkBeneficiary, Long> {

    @Query(value = "select * from bulk_beneficiaries where business_id=?1 and b_type=?2",
            countQuery = "select count(*) from bulk_beneficiaries where business_id=?1" +
                    "and b_type=?2",
            nativeQuery = true)
    Page<BulkBeneficiary> findByBusinessId(Long id, String type, Pageable pageable);

    @Query(value = "select * from bulk_beneficiaries where business_id=?1 and bulk_category_id=?2 and b_type=?3",
            countQuery = "select count(*) from bulk_beneficiaries where business_id=?1 and bulk_category_id=?2 and b_type=?3",
            nativeQuery = true)
    Page<BulkBeneficiary> findByBusinessIdAndCategoryId(Long businessId, Long categoryId, String type, Pageable pageable);

    @Query(value = "select * from bulk_beneficiaries where bulk_category_id=?1", nativeQuery = true)
    Collection<BulkBeneficiary> findByCategoryId(Long id);
    @Query(value = "select * from bulk_beneficiaries where bulk_category_id=?1 and phone_number=?2", nativeQuery = true)
    BulkBeneficiary findByBulkCategoryIdAndPhoneNumber(Long categoryId, String phoneNumber);
}
