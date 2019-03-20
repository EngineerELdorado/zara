package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Agent;
import com.zara.Zara.entities.Business;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.services.IAgentService;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.utils.FileStorageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "*")
public class UploadFileController {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    ICustomerService customerService;
    @Autowired
    IBusinessService businessService;
    @Autowired
    IAgentService agentService;
    ApiResponse apiResponse = new ApiResponse();

    @PostMapping("/uploadProfilePic")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("user_type")String user_type,
                                        @RequestParam("id") String id) {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();
        if (user_type.equals("customer")){
            Customer customer = customerService.findByPhoneNumber(id);
            customer.setProfilePic(fileDownloadUri);
            customerService.save(customer);
        }
        else  if (user_type.equals("business")){
            Business business = businessService.findByBusinessNumber(id);
            business.setProfilePic(fileDownloadUri);
            businessService.save(business);
        }
        else  if (user_type.equals("agent")){
            Agent agent = agentService.findByAgentNumber(id);
            agent.setProfilePic(fileDownloadUri);
            agentService.save(agent);
        }
        apiResponse.setResponseCode("00");
        apiResponse.setResponseMessage(fileDownloadUri);


        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
