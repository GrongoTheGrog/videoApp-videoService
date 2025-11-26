package com.hugo.video_service.common.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends HttpException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
