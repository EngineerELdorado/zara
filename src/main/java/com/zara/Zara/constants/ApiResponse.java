package com.zara.Zara.constants;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zara.Zara.entities.*;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collection;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private String responseCode;
    private String responseMessage;
    private Customer customer;
    private Agent agent;
    private Business business;
    private Developer developer;
    private BulkCategory bulkCategory;
    private BulkBeneficiary beneficiary;
    private Collection<PesapayTransaction>transactions;
    private Collection<Customer>customers;
    private Collection<BulkCategory>bulkCategories;
    private Collection<BulkBeneficiary>bulkBeneficiaries;
    private BigDecimal allStatsSum;
    private BigDecimal entriesStatsSum;
    private BigDecimal outsStatsSum;
    private Collection<PesapayTransaction>allRecentTransactions;
    private Collection<PesapayTransaction>entriesRecentTransactions;
    private Collection<PesapayTransaction>outsRecentTransactions;
    private Admin admin;
    private int totalCount;

}
