package com.zara.Zara.models;

import lombok.Data;

@Data
public class ChangePinObjet {

    private Long id;
    private String identifier;
    private String oldPin;
    private String newPin;
    private String oldPassword;
    private String newPassword;

}
