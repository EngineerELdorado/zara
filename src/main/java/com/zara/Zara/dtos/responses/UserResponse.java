package com.zara.Zara.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long userId;
    private String firstName;
    private String lastName;
    private boolean onboarded;
}