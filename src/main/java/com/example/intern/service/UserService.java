package com.example.intern.service;

import com.example.intern.dto.SignUpRequestDto;
import com.example.intern.dto.SignUpResponseDto;
import com.example.intern.entity.User;
import com.example.intern.entity.UserRole;
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
        return User.builder()
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .role(UserRole.ROLE_USER)
                .build();
    }
}
