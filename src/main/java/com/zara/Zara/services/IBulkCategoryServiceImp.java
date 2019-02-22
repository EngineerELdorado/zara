package com.zara.Zara.services;

import com.zara.Zara.entities.BulkCategory;
import com.zara.Zara.repositories.BulkCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class IBulkCategoryServiceImp implements IBulkCategoryService {

    @Autowired
    BulkCategoryRepository bulkCategoryRepository;
    @Override
    public BulkCategory save(BulkCategory bulkCategory) {
        return bulkCategoryRepository.save(bulkCategory);
    }

    @Override
    public BulkCategory findById(Long id) {
        return bulkCategoryRepository.getOne(id);
    }

    @Override
    public Collection<BulkCategory> findByBusinessId(Long id) {
        return bulkCategoryRepository.findByBusinessId(id);
    }
}