package com.example.intern.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    FAIL(500, "실패했습니다."),
    ACCESS_DINIED(403, "접근권한이 없습니다."),
    USER_NOT_FOUND(400, "해당 유저를 찾지 못했습니다."),
    INVALID_PASSWORD(400, "잘못된 비밀번호입니다."),
    DUPLICATE_UESR(400, "중복된 이름입니다."),
    DUPLICATE_NICKNAME(400,"중복된 닉네임입니다." );
    private int status;
    private String msg;
}
