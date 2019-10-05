package com.zara.Zara.controllers.administration;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Admin;
import com.zara.Zara.models.ChangePinObjet;
import com.zara.Zara.models.LoginObject;
import com.zara.Zara.services.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/admins/users")
@CrossOrigin(origins = "*")
public class _AdminController {

    @Autowired
    IAdminService adminService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    ApiResponse apiResponse = new ApiResponse();

    @PostMapping("/create")
    public ResponseEntity<?>save(@RequestBody Admin admin,@RequestParam String createdBy){

        if(adminService.findByUsername(admin.getEmail())==null){
            admin.setCreatedOn(new Date());
            admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));
            admin.setStatus("ACTIVE");
            adminService.save(admin);
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("Admin Created");
        }else{
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Email indisponible");
        }


        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody LoginObject loginObject){

        Admin admin = adminService.findByUsername(loginObject.getEmail());
        if(admin==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Utilisateur inconnu");
        }else{
            if(bCryptPasswordEncoder.matches(loginObject.getPassword(), admin.getPassword())){
                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage("Bien venu "+admin.getFullName());
                apiResponse.setData(admin);
            }else{
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("Email ou mot de passe incorrect");
            }
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/block/{id}")
    public ResponseEntity<?>blockAdmin(@PathVariable Long id){
        Admin admin = adminService.findOne(id);

        if (admin.getStatus().equalsIgnoreCase("ACTIVE")){
            admin.setStatus("INACTIVE");
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("COMPTE BLOQUE");
        }else if (admin.getStatus().equalsIgnoreCase("INACTIVE")){
            admin.setStatus("ACTIVE");
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("COMPTE DEBLOQUE");
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/find-all")
    public ResponseEntity<?>getAllAdmins(@RequestParam int page, @RequestParam int size,
                                         @RequestParam (required = false) String param){

        Page<Admin>admins;
        if (param.length()>0){
            admins = adminService.filter(page,size,param);
        }else{
            admins = adminService.findAll(page,size);
        }
        apiResponse.setData(admins);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @PostMapping("/change-password")
    public ResponseEntity<?>changePassword(@RequestBody ChangePinObjet pinObjet){

        Admin admin = adminService.findOne(pinObjet.getId());
        if(bCryptPasswordEncoder.matches(pinObjet.getOldPassword(),admin.getPassword())){
            admin.setPassword(bCryptPasswordEncoder.encode(pinObjet.getNewPassword()));
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("MOT DE PASSE CHANGE");
        }else{
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("ANCIEN MOT DE PASSE INCORRECT");
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
