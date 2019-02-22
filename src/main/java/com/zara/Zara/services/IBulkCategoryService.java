package com.zara.Zara.services;

import com.zara.Zara.entities.BulkCategory;

import java.util.Collection;

public interface IBulkCategoryService {

    BulkCategory save(BulkCategory bulkCategory);
    BulkCategory findById(Long id);
    Collection<BulkCategory> findByBusinessId(Long id);
}
