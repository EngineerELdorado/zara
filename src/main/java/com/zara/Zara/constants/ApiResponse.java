package com.zara.Zara.constants;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zara.Zara.entities.*;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    private Admin admin;
    private Page pagedTransactions;


}
