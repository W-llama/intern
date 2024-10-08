package com.example.intern.dto;

import com.example.intern.entity.User;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SignUpResponseDto {

    private String username;
    private String nickname;
    private List<AuthorityDto> authorities;

    public SignUpResponseDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();

        this.authorities = user.getAuthorities().stream()
                .map(authority -> new AuthorityDto(authority.getAuthority()))
                .collect(Collectors.toList());
    }
}
