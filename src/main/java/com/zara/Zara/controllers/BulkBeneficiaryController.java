package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.BulkBeneficiary;
import com.zara.Zara.entities.BulkCategory;
import com.zara.Zara.entities.Business;
import com.zara.Zara.repositories.BulkBeneficiaryRepository;
import com.zara.Zara.services.IBulkBeneficiaryService;
import com.zara.Zara.services.IBulkCategoryService;
import com.zara.Zara.services.IBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bulkBeneficiaries")
public class BulkBeneficiaryController {

    @Autowired
    IBulkBeneficiaryService bulkBeneficiaryService;
    @Autowired
    IBulkCategoryService categoryService;
    @Autowired
    IBusinessService businessService;
    ApiResponse apiResponse = new ApiResponse();
    @PostMapping("/post/{businessId}/{categoryId}")
    public ResponseEntity<?>post (@RequestBody BulkBeneficiary bulkBeneficiary,
                                  String businessId, Long categoryId){
        Business business = businessService.findByBusinessNumber(businessId);
        BulkCategory category = categoryService.findById(categoryId);
        if (business==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce business n'existe pas");
        }else{
            if (category==null){
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("Cette categorie n'existe pas");
            }else{
                BulkBeneficiary beneficiary = bulkBeneficiaryService.save(bulkBeneficiary);
                if (beneficiary!=null){
                    apiResponse.setResponseCode("00");
                    apiResponse.setResponseMessage("beneficiare cree");
                }else {
                    apiResponse.setResponseCode("01");
                    apiResponse.setResponseMessage("operation echouee");
                }
            }
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
