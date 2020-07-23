package com.zara.Zara.converters;

import com.zara.Zara.dtos.responses.UserResponse;
import com.zara.Zara.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserResourceConverter {

    public UserResponse convert(User user) {
        return UserResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .onboarded(user.isOnboarded())
                .build();
    }
}
