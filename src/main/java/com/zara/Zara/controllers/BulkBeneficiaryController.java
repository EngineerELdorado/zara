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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bulkBeneficiaries")
@CrossOrigin(origins = "*")
public class BulkBeneficiaryController {

    @Autowired
    IBulkBeneficiaryService bulkBeneficiaryService;
    @Autowired
    IBulkCategoryService categoryService;
    @Autowired
    IBusinessService businessService;
    @Autowired
    ICustomerService customerService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    ApiResponse apiResponse = new ApiResponse();
    Logger LOG = LogManager.getLogger(CustomerController.class);
    @PostMapping("/post")
    public ResponseEntity<?>post (@RequestBody BulkBeneficiary bulkBeneficiary){

        LOG.info("CATEGORY_ID=> "+bulkBeneficiary.getCategoryId());
        BulkCategory category = categoryService.findById(bulkBeneficiary.getCategoryId());
        LOG.info("CATEGORY_ID=> "+category.toString());
        Business business = category.getBusiness();
        if (business==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce business n'existe pas");
        }else if (!bCryptPasswordEncoder.matches(bulkBeneficiary.getBusinessPin(), business.getPin())){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Pin Incorrect");
        }else if (bulkBeneficiaryService.findByCategoryIdAndPhoneNumber(category.getId(), bulkBeneficiary.getPhoneNumber())!=null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce beneficiaire existe deja dans cette categorie");
        }
        else{
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
                             bulkBeneficiary.setBusiness(business);
                             bulkBeneficiary.setBulkCategory(category);
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
