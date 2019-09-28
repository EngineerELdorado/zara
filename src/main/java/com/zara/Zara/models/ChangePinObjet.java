package com.zara.Zara.models;

import lombok.Data;

@Data
public class ChangePinObjet {

    private String identifier;
    private String oldPin;
    private String newPin;

}
