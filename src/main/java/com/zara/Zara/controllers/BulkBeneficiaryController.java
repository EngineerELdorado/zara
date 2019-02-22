package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.BulkBeneficiary;
import com.zara.Zara.entities.BulkCategory;
import com.zara.Zara.entities.Business;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.repositories.BulkBeneficiaryRepository;
import com.zara.Zara.services.IBulkBeneficiaryService;
import com.zara.Zara.services.IBulkCategoryService;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bulkBeneficiaries")
public class BulkBeneficiaryController {

    @Autowired
    IBulkBeneficiaryService bulkBeneficiaryService;
    @Autowired
    IBulkCategoryService categoryService;
    @Autowired
    IBusinessService businessService;
    @Autowired
    ICustomerService customerService;
    ApiResponse apiResponse = new ApiResponse();
    @PostMapping("/post/{categoryId}")
    public ResponseEntity<?>post (@RequestBody BulkBeneficiary bulkBeneficiary,
                                  @PathVariable String categoryId){
        Business business = categoryService.findById(Long.parseLong(categoryId)).getBusiness();
        BulkCategory category = categoryService.findById(Long.parseLong(categoryId));
        if (business==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce business n'existe pas");
        }else{
            Customer customer = customerService.findByPhoneNumber(bulkBeneficiary.getPhoneNumber());
                     if (customer==null){
                         apiResponse.setResponseCode("01");
                         apiResponse.setResponseMessage("Ce numero n'a pas de compte client sur PesaPay");
                     }else if (!customer.getStatus().equals("ACTIVE")){
                         apiResponse.setResponseCode("01");
                         apiResponse.setResponseMessage("Ce compte n'est pas ACTIF. veillez demander A "+customer.getFullName()+"" +
                                 "de faire activer son compte en contactant le bureau de PesaPay");
                     }else if (!customer.isVerified()){
                         apiResponse.setResponseCode("01");
                         apiResponse.setResponseMessage("Ce compte n'est pas encore verifiE. veillez demander A "+customer.getFullName()+"" +
                                 "de faire verifier son compte en contactant le bureau de PesaPay");
                     }else {

                         if (category==null){
                             apiResponse.setResponseCode("01");
                             apiResponse.setResponseMessage("Cette categorie n'existe pas");
                         }else{
                             bulkBeneficiary.setName(customer.getFullName());
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

        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
