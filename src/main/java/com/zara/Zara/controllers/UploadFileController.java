package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Agent;
import com.zara.Zara.entities.Business;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.services.IAgentService;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "*")
public class UploadFileController {

    @Autowired
    ICustomerService customerService;
    @Autowired
    IBusinessService businessService;
    @Autowired
    IAgentService agentService;
    ApiResponse apiResponse = new ApiResponse();

    static String UPLOAD_PATH="src/resources/uploads";

    @PostMapping("/uploadProfilePic")
    public ResponseEntity<?> uploadFile(@RequestParam("file") String file,
                                        @RequestParam("user_type")String user_type,
                                        @RequestParam("id") String id) throws IOException {

        if (user_type.equals("customer")){
            Customer customer = customerService.findByPhoneNumber(id);
            customer.setProfilePic(file);
            customerService.save(customer);
        }
        else  if (user_type.equals("business")){
            Business business = businessService.findByBusinessNumber(id);
            business.setProfilePic(file);
            businessService.save(business);
        }
        else  if (user_type.equals("agent")){
            Agent agent = agentService.findByAgentNumber(id);
            agent.setProfilePic(file);
            agentService.save(agent);
        }
        apiResponse.setResponseCode("00");
        apiResponse.setResponseMessage("Photo successfully saved");


        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    private String getRandomString() {
        return new Random().nextInt(999999) + "_" + System.currentTimeMillis();
    }

    private File getTargetFile(String fileExtn, String fileName) {
        File targetFile = new File(UPLOAD_PATH + fileName + fileExtn);
        return targetFile;
    }

    private String getFileExtension(MultipartFile inFile) {
        String fileExtention = inFile.getOriginalFilename().substring(inFile.getOriginalFilename().lastIndexOf('.'));
        return fileExtention;
    }

    /**
     * Reads the relative path to the resource directory from the <code>RESOURCE_PATH</code> file located in
     * <code>src/main/resources</code>
     * @return the relative path to the <code>resources</code> in the file system, or
     *         <code>null</code> if there was an error
     */

}
