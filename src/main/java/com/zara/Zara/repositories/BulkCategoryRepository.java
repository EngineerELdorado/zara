package com.zara.Zara.repositories;

import com.zara.Zara.entities.BulkCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface BulkCategoryRepository extends JpaRepository<BulkCategory, Long> {

    @Query(value = "select * from bulk_categories where business_id=?1", nativeQuery = true)
    Collection<BulkCategory> findByBusinessId(Long id);

    @Query(value = "select * from bulk_categories where business_id=?1", nativeQuery = true)
    @Override
    BulkCategory getOne(Long id);
}
