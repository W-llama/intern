package com.example.intern.controller;

import com.example.intern.dto.LoginRequestDto;
import com.example.intern.dto.LoginResponseDto;
import com.example.intern.dto.SignUpRequestDto;
import com.example.intern.entity.User;
import com.example.intern.entity.UserRole;
import com.example.intern.jwt.JwtUtil;
import com.example.intern.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {
    MockMvc mvc;

    @Mock
    UserService userService;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("SignUp")
    void signUp() throws Exception {
        // given
        SignUpRequestDto requestDto = new SignUpRequestDto("newUser1", "Nickname", "NewPassword!1");

        User user = User.builder()
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .role(UserRole.ROLE_USER)
                .build();

        String json = new Gson().toJson(requestDto);

        // when & then
        mvc.perform(post("/api/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 테스트")
    void login() throws Exception {
        // given
        String username = "newUser1";
        String password = "NewPassword!1";
        LoginRequestDto requestDto = new LoginRequestDto(username, password);

        String token = JwtUtil.createToken(username, UserRole.ROLE_USER, JwtUtil.ACCESS_TOKEN_EXPIRATION);

        // Mocking 로그인 성공 응답
        LoginResponseDto responseDto = new LoginResponseDto(token);
        when(userService.login(any(LoginRequestDto.class))).thenReturn(responseDto);

        String json = new Gson().toJson(requestDto);

        // when & then
        mvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username)) // 응답의 username 확인
                .andExpect(jsonPath("$.token").value(token)) // 응답의 token 확인
                .andDo(print());
    }
}