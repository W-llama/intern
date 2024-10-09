package com.example.intern.controller;

import com.example.intern.dto.LoginRequestDto;
import com.example.intern.dto.LoginResponseDto;
import com.example.intern.dto.SignUpRequestDto;
import com.example.intern.dto.SignUpResponseDto;
import com.example.intern.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name ="User", description = "UserController APIs")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "User SignUp", description = "회원가입 기능입니다.")
    public ResponseEntity<SignUpResponseDto> signup(@Valid @RequestBody SignUpRequestDto requestDto) {
        SignUpResponseDto responseDto = userService.signUp(requestDto);
        return ResponseEntity.ok().body(responseDto);
    }

    // 로그인
    @PostMapping("/login")
    @Operation(summary = "User Login", description = "로그인 기능입니다.")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        LoginResponseDto responseDto = userService.login(requestDto);
        return ResponseEntity.ok().body(responseDto);
    }
}