package com.zara.Zara.controllers;


import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Business;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    INotificationService notificationService;
    @Autowired
    IBusinessService businessService;
    ApiResponse apiResponse = new ApiResponse();
    @GetMapping("/findByBusiness/{businessNumber}")
    public ResponseEntity<?> findByBusiness(String businessNumber, int page, int size){
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,"id"));
        Pageable pageable = new PageRequest(page,size,sort);
        Business business = businessService.findByBusinessNumber(businessNumber);
        apiResponse.setResponseCode("00");
        apiResponse.setResponseMessage("");
        apiResponse.setNotifications(notificationService.findByBusiness(business.getId(),pageable).getContent());

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
