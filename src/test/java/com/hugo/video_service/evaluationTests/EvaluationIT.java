package com.hugo.video_service.evaluationTests;


import com.hugo.video_service.ContainerUtils;
import com.hugo.video_service.TestUtils;
import com.hugo.video_service.common.exceptions.HttpException;
import com.hugo.video_service.evaluations.Evaluation;
import com.hugo.video_service.evaluations.EvaluationType;
import com.hugo.video_service.evaluations.dto.LikeRequestDto;
import com.hugo.video_service.evaluations.repositories.EvaluationRepository;
import com.hugo.video_service.videos.Video;
import com.hugo.video_service.videos.repositories.VideoRepository;
import com.hugo.video_service.videos.services.CloudfrontService;
import com.hugo.video_service.videos.services.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.assertj.core.api.Assertions.*;

import org.testcontainers.mongodb.MongoDBAtlasLocalContainer;

import java.util.Optional;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
public class EvaluationIT {

    public static final MongoDBAtlasLocalContainer mongoDbContainer = ContainerUtils.setUpMongoAtlas();

    @Autowired
    public EvaluationRepository evaluationRepository;

    @Autowired
    public VideoRepository videoRepository;

    @Autowired
    public VideoService videoService;

    @MockitoBean
    public CloudfrontService cloudfrontService;

    @Test
    public void testIfEvaluationsArePersisted(){
        Video video = TestUtils.getVideo();
        videoRepository.save(video);

        videoService.LikeVideo(LikeRequestDto.builder().evaluationType(EvaluationType.LIKE).build(), "123", video.getId());

        Optional<Evaluation> evaluation = evaluationRepository.findByUserIdAndVideoId("123", video.getId());

        assertThat(evaluation).isPresent();
        assertThat(evaluation.get().getEvaluationType()).isEqualTo(EvaluationType.LIKE);
    }

    @Test
    public void testIfEvaluationsCanToggle(){
        Video video = TestUtils.getVideo();
        videoRepository.save(video);

        videoService.LikeVideo(LikeRequestDto.builder().evaluationType(EvaluationType.LIKE).build(), "123", video.getId());
        videoService.LikeVideo(LikeRequestDto.builder().evaluationType(EvaluationType.DISLIKE).build(), "123", video.getId());

        Optional<Evaluation> evaluation = evaluationRepository.findByUserIdAndVideoId("123", video.getId());

        assertThat(evaluation).isPresent();
        assertThat(evaluation.get().getEvaluationType()).isEqualTo(EvaluationType.DISLIKE);
    }

    @Test
    public void testIfEvaluationsThrowIfSame(){
        Video video = TestUtils.getVideo();
        videoRepository.save(video);

        assertThatThrownBy(() -> {
            videoService.LikeVideo(LikeRequestDto.builder().evaluationType(EvaluationType.LIKE).build(), "123", video.getId());
            videoService.LikeVideo(LikeRequestDto.builder().evaluationType(EvaluationType.LIKE).build(), "123", video.getId());
        }).isInstanceOf(HttpException.class);

    }

}
