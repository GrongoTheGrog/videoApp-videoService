package com.hugo.video_service.videoTests;


import com.hugo.video_service.ContainerUtils;
import com.hugo.video_service.TestUtils;
import com.hugo.video_service.videos.Video;
import com.hugo.video_service.videos.dto.WatchVideoDtoResponse;
import com.hugo.video_service.videos.repositories.VideoRepository;
import com.hugo.video_service.videos.services.CloudfrontService;
import com.hugo.video_service.videos.services.QueueService;
import com.hugo.video_service.videos.services.S3Service;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.mongodb.MongoDBAtlasLocalContainer;
import static org.assertj.core.api.Assertions.*;
import org.testcontainers.rabbitmq.RabbitMQContainer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
public class VideoIT {

    private static final RabbitMQContainer rabbitMQContainer = ContainerUtils.setUpRabbitMq();
    private static final MongoDBAtlasLocalContainer mongoDbContainer = ContainerUtils.setUpMongoAtlas();

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoSpyBean
    private QueueService queueService;

    @MockitoBean
    private S3Service s3Service;

    @MockitoBean
    private CloudfrontService cloudfrontService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void clear(){
        videoRepository.deleteAll();
    }

    @Test
    public void testIfUserCanBeCreated() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.multipart("/videos")
                .file(TestUtils.getTestVideo())
                .param("title", "test")
                .param("description", "test description")
                .param("videoQuality", "R1080p", "R720p", "R480p")
                .header("user_id", "12345678")
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        List<Video> videos = videoRepository.findAll();

        assertThat(videos.size()).isEqualTo(1);
        assertThat(videos.getFirst().getTitle()).isEqualTo("test");
        assertThat(videos.getFirst().getDescription()).isEqualTo("test description");

        Mockito.verify(s3Service).saveFile(ArgumentMatchers.any(Path.class), ArgumentMatchers.anyString());
    }

    @Test
    public void testIfManifestLocationCanBeRetrieved() throws Exception {
        Video video = TestUtils.getVideo();
        videoRepository.save(video);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/videos/" + video.getId() + "/watch")
                        .header("user_id", "123"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        WatchVideoDtoResponse watchVideoDtoResponse = objectMapper.readValue(result.getResponse().getContentAsString(), WatchVideoDtoResponse.class);

        assertThat(watchVideoDtoResponse).isNotNull();
    }

    @Test
    public void testIfVideoCanBeDeleted() throws Exception {
        Video video = TestUtils.getVideo();
        videoRepository.save(video);

        mockMvc.perform(MockMvcRequestBuilders.delete("/videos/" + video.getId())
                .header("user_id", video.getUserId())
        ).andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(s3Service).deleteVideoByIdAndUserId(video.getId(), video.getUserId());

        Optional<Video> deletedVideo = videoRepository.findById(video.getId());

        assertThat(deletedVideo.isEmpty()).isTrue();
    }

    @Test
    public void testIfVideoCanBeRetrieved() throws Exception {
        Video video = TestUtils.getVideo();
        videoRepository.save(video);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/videos/" + video.getId())
                .header("user-id", video.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Video returnedVideo = objectMapper.readValue(result.getResponse().getContentAsString(), Video.class);

        assertThat(returnedVideo.getId()).isEqualTo(video.getId());
        assertThat(returnedVideo.getTitle()).isEqualTo(video.getTitle());
        assertThat(returnedVideo.getDescription()).isEqualTo(video.getDescription());
    }

}
