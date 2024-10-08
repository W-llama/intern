package com.example.intern.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponseDto {
    private String token;
    //private String refreshToken;

    public LoginResponseDto(String token) {
        this.token = token;
        //this.refreshToken = refreshToken;
    }
}
