package com.hugo.video_service.exceptions;

import org.springframework.http.HttpStatus;

public class HttpException extends RuntimeException {
    private HttpStatus httpStatus;

    public HttpException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}
