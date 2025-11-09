package com.hugo.video_service.service;


import com.hugo.video_service.dto.UploadVideoDto;
import com.hugo.video_service.dto.UploadVideoEvent;
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
            String videoPath
    ){
        UploadVideoEvent uploadVideoEvent = UploadVideoEvent.builder()
                        .videoQualities(uploadVideoDto.getVideoQuality())
                        .messageId(UUID.randomUUID().toString())
                        .userId(userId)
                        .videoPath(videoPath)
                        .segmentDuration(6)
                        .build();

        rabbitTemplate.convertAndSend(uploadVideoEvent);
        log.info("Upload video event created for file {}.", videoPath);

    }

}
