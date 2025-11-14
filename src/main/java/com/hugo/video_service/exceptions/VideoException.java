package com.hugo.video_service.exceptions;

import org.springframework.http.HttpStatus;

public class VideoException extends HttpException {
    public VideoException(String message) {
        super(
                message,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
