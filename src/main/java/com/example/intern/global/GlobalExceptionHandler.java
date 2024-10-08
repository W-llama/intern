package com.example.intern.global;


import com.example.intern.global.dto.ExceptionResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponseDto> AccessDeniedException(HttpServletRequest request, Exception e){
        e.printStackTrace();
        ExceptionResponseDto exceptionResponse = ExceptionResponseDto.builder()
                .msg(ErrorCode.ACCESS_DINIED.getMsg())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }




    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ExceptionResponseDto> defaultException(HttpServletRequest request, Exception e){
        e.printStackTrace();
        ExceptionResponseDto exceptionResponse = ExceptionResponseDto.builder()
                .msg(ErrorCode.FAIL.getMsg())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponseDto> handleInvalidPasswordException(HttpServletRequest request, CustomException e) {
        ExceptionResponseDto exceptionResponse = ExceptionResponseDto.builder()
                .msg(e.getErrorCode().getMsg())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(exceptionResponse, HttpStatusCode.valueOf(e.getErrorCode().getStatus()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(e.getBindingResult().getFieldErrors().get(0).getDefaultMessage() ,HttpStatus.BAD_REQUEST);
    }


}
