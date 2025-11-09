package com.hugo.video_service.controller;


import com.hugo.video_service.dto.ExceptionResponse;
import com.hugo.video_service.exceptions.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {


    @ExceptionHandler(HttpException.class)
    public ExceptionResponse httpException(
            HttpException e
    ){
        return ExceptionResponse.builder()
                .httpStatus(e.getHttpStatus())
                .message(e.getMessage())
                .build();
    }


    @ExceptionHandler(Exception.class)
    public ExceptionResponse exception(
            Exception e
    ){
        return ExceptionResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();
    }
}
