package com.hugo.video_service.controller;


import com.hugo.video_service.dto.ExceptionResponse;
import com.hugo.video_service.exceptions.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
public class ExceptionController {


    @ExceptionHandler(HttpException.class)
    public ResponseEntity<ExceptionResponse> httpException(
            HttpException e
    ){
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .exceptionId(UUID.randomUUID().toString())
                .httpStatus(e.getHttpStatus())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(e.getHttpStatus()).body(exceptionResponse);
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse exception(
            Exception e
    ){
        return ExceptionResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .exceptionId(UUID.randomUUID().toString())
                .build();
    }
}
