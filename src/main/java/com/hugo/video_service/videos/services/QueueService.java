package com.hugo.video_service.videos.services;


import com.hugo.video_service.videos.dto.UploadVideoDto;
import com.hugo.video_service.videos.dto.UploadVideoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class QueueService {

    private final RabbitTemplate rabbitTemplate;

    public void postUploadVideoEvent(
            String userId,
            UploadVideoDto uploadVideoDto,
            String videoDir
    ){
        UploadVideoEvent uploadVideoEvent = UploadVideoEvent.builder()
                        .videoQualities(uploadVideoDto.getVideoQuality())
                        .messageId(UUID.randomUUID().toString())
                        .userId(userId)
                        .videoPath(videoDir + "/mp4")
                        .videoDir(videoDir)
                        .segmentDuration(6)
                        .build();

        rabbitTemplate.convertAndSend(uploadVideoEvent);
        log.info("Upload video event created for file {}.", videoDir);
    }

}
