package com.zara.Zara.services;

import com.zara.Zara.entities.BulkCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface IBulkCategoryService {

    BulkCategory save(BulkCategory bulkCategory);
    BulkCategory findById(Long id);
    Page<BulkCategory> findByBusinessId(Long id, Pageable pageable);
    void delete(Long id);
}
