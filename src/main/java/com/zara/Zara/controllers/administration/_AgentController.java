package com.zara.Zara.controllers.administration;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Agent;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.services.IAgentService;
import com.zara.Zara.services.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admins/agents")
@RestController
@CrossOrigin(origins = "*")
public class _AgentController {

    @Autowired
    IAgentService agentService;
    ApiResponse apiResponse = new ApiResponse();
    @GetMapping("/find-all")
    public ResponseEntity<?>findAll(@RequestParam int page,
                                    @RequestParam int size,
                                    @RequestParam Long start,
                                    @RequestParam Long end,
                                    @RequestParam (required = false) String param){

        Page<Agent>agents = agentService.findAll(page,size,start,end,param);
//        apiResponse.setResponseCode("00");
        apiResponse.setData(agents);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/block/{id}")
    public ResponseEntity<?>block(@PathVariable Long id){
        Agent agent = agentService.findOne(id);
        if (agent.getStatus().equalsIgnoreCase("ACTIVE")){
            agent.setStatus("INACTIVE");
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("COMPTE BLOQUE");
        }else if (agent.getStatus().equalsIgnoreCase("INACTIVE")){
            agent.setStatus("ACTIVE");
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("COMPTE DEBLOQUE");
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
