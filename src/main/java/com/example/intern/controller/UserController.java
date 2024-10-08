package com.example.intern.controller;

import com.example.intern.dto.LoginRequestDto;
import com.example.intern.dto.LoginResponseDto;
import com.example.intern.dto.SignUpRequestDto;
import com.example.intern.dto.SignUpResponseDto;
import com.example.intern.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signup(@Valid @RequestBody SignUpRequestDto requestDto) {
        SignUpResponseDto responseDto = userService.signUp(requestDto);
        return ResponseEntity.ok().body(responseDto);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        LoginResponseDto responseDto = userService.login(requestDto);
        return ResponseEntity.ok().body(responseDto);
    }
}