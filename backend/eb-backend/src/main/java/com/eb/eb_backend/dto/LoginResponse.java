package com.eb.eb_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    private String type = "Bearer";
    private UserDto user;
    
    public LoginResponse(String token, UserDto user) {
        this.token = token;
        this.user = user;
    }
}



















































