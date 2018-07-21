package com.zara.Zara.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangePinRequest {

    private String oldPin;
    private String newPin;
}
