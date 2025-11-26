package com.hugo.video_service.videos.exceptions;

import com.hugo.video_service.common.exceptions.HttpException;
import org.springframework.http.HttpStatus;

public class VideoException extends HttpException {
    public VideoException(String message) {
        super(
                message,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
