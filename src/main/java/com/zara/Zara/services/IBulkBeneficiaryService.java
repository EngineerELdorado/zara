package com.zara.Zara.services;

import com.zara.Zara.entities.BulkBeneficiary;

import java.util.Collection;

public interface IBulkBeneficiaryService {

    BulkBeneficiary save(BulkBeneficiary bulkBeneficiary);
    BulkBeneficiary findById(Long id);
    Collection<BulkBeneficiary>findByBusinessId(Long id);
    Collection<BulkBeneficiary>findByCaterory(Long id);
}
