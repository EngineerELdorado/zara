package com.zara.Zara.services;

import com.zara.Zara.entities.BulkBeneficiary;
import com.zara.Zara.repositories.BulkBeneficiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class BulkBeneficiaryServiceImp implements IBulkBeneficiaryService {

    @Autowired
    BulkBeneficiaryRepository bulkBeneficiaryRepository;

    @Override
    public BulkBeneficiary save(BulkBeneficiary bulkBeneficiary) {
        return bulkBeneficiaryRepository.save(bulkBeneficiary);
    }

    @Override
    public BulkBeneficiary findById(Long id) {
        return bulkBeneficiaryRepository.getOne(id);
    }

    @Override
    public Page<BulkBeneficiary> findByBusinessId(Long id, Pageable pageable) {
        return bulkBeneficiaryRepository.findByBusinessId(id, pageable);
    }

    @Override
    public Collection<BulkBeneficiary> findByCaterory(Long id) {
        return bulkBeneficiaryRepository.findByCategoryId(id);
    }

    @Override
    public BulkBeneficiary findByCategoryIdAndPhoneNumber(Long categoryId, String phoneNumber) {
        return bulkBeneficiaryRepository.findByBulkCategoryIdAndPhoneNumber(categoryId, phoneNumber);
    }

    @Override
    public Page<BulkBeneficiary> findByBusinessAndCategory(Long businessId, Long categoryId, Pageable pageable) {
        return bulkBeneficiaryRepository.findByBusinessIdAndCategoryId(businessId, categoryId,pageable);
    }

    @Override
    public void delete(Long id) {
        bulkBeneficiaryRepository.deleteById(id);
    }


}
