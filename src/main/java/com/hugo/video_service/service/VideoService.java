package com.hugo.video_service.service;


import com.hugo.video_service.domain.Video;
import com.hugo.video_service.dto.UploadVideoDto;
import com.hugo.video_service.exceptions.ForbiddenException;
import com.hugo.video_service.exceptions.NotFoundException;
import com.hugo.video_service.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoService {

    private static final Logger log = LogManager.getLogger(VideoService.class);
    private final AwsService awsService;
    private final VideoRepository videoRepository;
    private final QueueService queueService;

    @Transactional
    public Video postVideo(
            UploadVideoDto uploadVideoDto,
            String userId
    ) throws IOException {
        Video video = Video.builder()
                    .userId(userId)
                    .sizeInBytes(uploadVideoDto.getFile().getSize())
                    .title(uploadVideoDto.getTitle())
                    .description(uploadVideoDto.getDescription())
                    .build();

        videoRepository.save(video);
        awsService.saveFile(uploadVideoDto.getFile(), userId + "/" + video.getId() + "/mp4");
        queueService.postUploadVideoEvent(userId, uploadVideoDto, userId + "/" + video.getId());

        return video;
    }

    public Video getVideo(String videoId){
        Optional<Video> video = videoRepository.findById(videoId);
        if (video.isEmpty()) throw new NotFoundException("Video not found with id: " + videoId);

        return video.get();
    }

    public void deleteVideo(String videoId, String userId){
        Optional<Video> video = videoRepository.findById(videoId);
        if (video.isEmpty()) return;

        if (!video.get().getUserId().equals(userId))
            throw new ForbiddenException("User must be the owner of the resource.");

        videoRepository.delete(video.get());


        log.info("Video deleted.");
    }

}
