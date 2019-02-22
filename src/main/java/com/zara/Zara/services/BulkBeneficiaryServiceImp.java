package com.zara.Zara.services;

import com.zara.Zara.entities.BulkBeneficiary;
import com.zara.Zara.repositories.BulkBeneficiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Collection<BulkBeneficiary> findByBusinessId(Long id) {
        return bulkBeneficiaryRepository.findByBusinessId(id);
    }

    @Override
    public Collection<BulkBeneficiary> findByCaterory(Long id) {
        return null;
    }
}
