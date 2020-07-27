package com.zara.Zara.controllers;


import com.zara.Zara.dtos.responses.StatsResource;
import com.zara.Zara.resources.StatsResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/stats")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StatsController {

    private final StatsResourceService statsResourceService;

    @GetMapping(value = "", headers = "Authorization")
    public ResponseEntity<StatsResource> findStats(Long accountId,
                                                   @RequestParam(value = "startDate")
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
                                                   @RequestParam(value = "endDate")
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        return new ResponseEntity<>(statsResourceService.findStats(accountId, startDate, endDate), HttpStatus.OK);
    }
}
