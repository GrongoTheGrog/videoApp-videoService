package com.hugo.video_service.videos.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Data
public class UploadVideoDto {
    MultipartFile file;
    List<VideoQuality> videoQuality;
    String title;
    String description;
}
