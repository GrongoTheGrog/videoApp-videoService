package com.hugo.video_service.common.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExceptionResponse {

    String exceptionId = UUID.randomUUID().toString();
    HttpStatus httpStatus;
    String message;
    LocalDateTime timeStamp = LocalDateTime.now();
    Map<String, List<String>> fieldErrors;

}
