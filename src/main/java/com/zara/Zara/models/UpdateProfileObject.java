package com.zara.Zara.models;

import lombok.Data;

@Data
public class UpdateProfileObject {

    private String phone;
    private String pic;

    private String email;
    private String gender;
    private String country;
    private String dob;
    private String pin;

    @Override
    public String toString() {
        return "UpdateProfileObject{" +
                "phone='" + phone + '\'' +
                ", pic='" + pic + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", country='" + country + '\'' +
                ", dob='" + dob + '\'' +
                ", pin='" + pin + '\'' +
                '}';
    }
}
