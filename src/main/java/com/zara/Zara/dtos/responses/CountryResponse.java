package com.zara.Zara.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CountryResponse {

    private String code;
    private String name;
    private String flag;
}
