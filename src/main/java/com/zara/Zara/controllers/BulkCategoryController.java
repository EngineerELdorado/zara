package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.BulkCategory;
import com.zara.Zara.entities.Business;
import com.zara.Zara.services.IBulkBeneficiaryService;
import com.zara.Zara.services.IBulkCategoryService;
import com.zara.Zara.services.IBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bulkCategories")
@CrossOrigin(origins = "*")
public class BulkCategoryController {

    @Autowired
    IBulkCategoryService bulkCategoryService;
    @Autowired
    IBusinessService businessService;
    ApiResponse apiResponse = new ApiResponse();

    @PostMapping("/post/{businessId}")
    public ResponseEntity<?>post(@RequestBody BulkCategory bulkCategory,@PathVariable String businessId){

        Business business = businessService.findByBusinessNumber(businessId);
        if (business==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce business n'existe pas");
        }else {
            bulkCategory.setBusiness(businessService.findByBusinessNumber(businessId));
           BulkCategory createdCategory = bulkCategoryService.save(bulkCategory);
           if (createdCategory!=null){
               apiResponse.setResponseCode("00");
               apiResponse.setResponseMessage("Category created");
               apiResponse.setBulkCategory(createdCategory);
           }else{
               apiResponse.setResponseCode("01");
               apiResponse.setResponseMessage("Failed to create. check database connectivity");
           }
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @GetMapping("/findByBusiness/{businessNumber}")
    public ResponseEntity<?>findByBusinbess(@PathVariable String businessNumber,
                                            @RequestParam("page") int page,
                                            @RequestParam("size") int size){
        Business business = businessService.findByBusinessNumber(businessNumber);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,"id"));
        Pageable pageable = new PageRequest(page,size,sort);
        if (business==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Business introuvable");
        }else{
            apiResponse.setBulkCategories(bulkCategoryService.findByBusinessId(business.getId(), pageable).getContent());
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/delete")
    public ResponseEntity<?>delete(@RequestParam String id){

        bulkCategoryService.delete(Long.valueOf(id));
        apiResponse.setResponseCode("00");
        apiResponse.setResponseMessage("Category deleted");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);


    }
}
