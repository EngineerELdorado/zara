package com.zara.Zara.repositories;

import com.zara.Zara.entities.BulkCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface BulkCategoryRepository extends JpaRepository<BulkCategory, Long> {

    @Query(value = "select * from bulk_categories where business_id=?1 and type=?2",
             countQuery = "select count(*) from bulk_categories where business_id=?1 and b_type=?2",
            nativeQuery = true)
    Page<BulkCategory> findByBusinessId(Long id, String type, Pageable pageable);

    @Query(value = "select * from bulk_categories where id=?1", nativeQuery = true)
    @Override
    BulkCategory getOne(Long id);
}
