package com.example.intern.dto;

import com.example.intern.entity.User;
import lombok.Getter;

@Getter
public class SignUpResponseDto {

    private String username;
    private String nickname;
    private String role;

    public SignUpResponseDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.role = user.getRole().name();
    }
}
