package com.zara.Zara.services;

import com.zara.Zara.entities.BulkBeneficiary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface IBulkBeneficiaryService {

    BulkBeneficiary save(BulkBeneficiary bulkBeneficiary);
    BulkBeneficiary findById(Long id);
    Page<BulkBeneficiary>findByBusinessId(Long id, Pageable pageable);
    Collection<BulkBeneficiary>findByCaterory(Long id);
    BulkBeneficiary findByCategoryIdAndPhoneNumber(Long categoryId, String phoneNumber);

    Page<BulkBeneficiary>findByBusinessAndCategory(Long businessId, Long categoryId, Pageable pageable);

    void delete(Long id);
}
