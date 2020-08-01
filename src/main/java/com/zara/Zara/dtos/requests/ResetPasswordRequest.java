package com.zara.Zara.dtos.requests;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String newPassword;
    private String confirmNewPassword;
}
