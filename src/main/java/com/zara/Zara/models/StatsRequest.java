package com.zara.Zara.models;

import lombok.Data;

import java.util.Date;

@Data
public class StatsRequest {

    private String businessNumber;
    private String start;
    private String end;
}
