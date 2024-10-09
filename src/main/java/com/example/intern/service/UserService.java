package com.example.intern.service;

import com.example.intern.dto.LoginRequestDto;
import com.example.intern.dto.LoginResponseDto;
import com.example.intern.dto.SignUpRequestDto;
import com.example.intern.dto.SignUpResponseDto;
import com.example.intern.entity.User;
import com.example.intern.entity.UserRole;
import com.example.intern.global.CustomException;
import com.example.intern.global.ErrorCode;
import com.example.intern.jwt.JwtUtil;
import com.example.intern.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpResponseDto signUp(SignUpRequestDto requestDto){
        User user = createUser(requestDto);
        userRepository.save(user);
        return new SignUpResponseDto(user);
    }

    private User createUser(SignUpRequestDto requestDto) {
        if(userRepository.findByUsername(requestDto.getUsername()).isPresent()){
            throw new CustomException(ErrorCode.DUPLICATE_UESR);
        }
        if(userRepository.findByNickname(requestDto.getNickname()).isPresent()){
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        return User.builder()
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .role(UserRole.ROLE_USER)
                .build();
    }

    public LoginResponseDto login(LoginRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String token = JwtUtil.createToken(user.getUsername(), JwtUtil.ACCESS_TOKEN_EXPIRATION);
        String substringJwtToken = JwtUtil.substringJwtToken(token);
        //String refreshToken = JwtUtil.createToken(user.getUsername(), JwtUtil.REFRESH_TOKEN_EXPIRATION);
        //user.updateRefresh(refreshToken);
        userRepository.save(user);

        return new LoginResponseDto(substringJwtToken);
    }

    public String logout(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // user의 refresh token을 없앤다.
        user.clearRefreshToken();
        userRepository.save(user);

        return "로그아웃 하였습니다.";
    }
}
