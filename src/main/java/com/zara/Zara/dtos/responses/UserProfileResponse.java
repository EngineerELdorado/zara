package com.zara.Zara.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String profilePic;
    private boolean isOnboarded;

}
