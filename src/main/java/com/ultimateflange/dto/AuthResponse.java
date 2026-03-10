package com.ultimateflange.dto;

import com.ultimateflange.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;  // Add this import

@Data
@Builder  // Add this annotation
@NoArgsConstructor  // Add this
@AllArgsConstructor
public class AuthResponse {
    private boolean success;
    private String message;
    private String token;
    private User user;
}