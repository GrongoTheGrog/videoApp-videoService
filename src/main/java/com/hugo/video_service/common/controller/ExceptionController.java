package com.hugo.video_service.common.controller;


import com.hugo.video_service.common.dto.ExceptionResponse;
import com.hugo.video_service.common.exceptions.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class ExceptionController {


    @ExceptionHandler(HttpException.class)
    public ResponseEntity<ExceptionResponse> httpException(
            HttpException e
    ){
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .exceptionId(UUID.randomUUID().toString())
                .httpStatus(e.getHttpStatus())
                .timeStamp(LocalDateTime.now())
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
                .timeStamp(LocalDateTime.now())
                .message(e.getMessage())
                .exceptionId(UUID.randomUUID().toString())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse invalidMethod(
            MethodArgumentNotValidException exception
    ){
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .exceptionId(UUID.randomUUID().toString())
                .timeStamp(LocalDateTime.now())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("Bad form fields provided.")
                .build();

        Map<String, List<String>> fields = new HashMap<>();

        for (FieldError error : exception.getBindingResult().getFieldErrors()){
            List<String> errorList = fields.computeIfAbsent(error.getField(), i -> new ArrayList<>());

            errorList.add(error.getDefaultMessage());
        }

        exceptionResponse.setFieldErrors(fields);

        return exceptionResponse;
    }
}
