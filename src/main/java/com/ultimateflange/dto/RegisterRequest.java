package com.ultimateflange.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String company;
    private String industry;
    private String userType;
}