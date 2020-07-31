package com.zara.Zara.resources;

import com.zara.Zara.dtos.requests.CommissionTransferRequest;
import com.zara.Zara.services.CommissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommissionResourceService {

    private final CommissionService commissionService;

    public void transfer(Long accountId, CommissionTransferRequest request) {
        commissionService.transfer(accountId, request);
    }
}
