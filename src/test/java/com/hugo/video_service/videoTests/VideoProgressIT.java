package com.hugo.video_service.videoTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugo.video_service.ContainerUtils;
import com.hugo.video_service.videos.VideoProgress;
import com.hugo.video_service.videos.dto.VideoProgressDto;
import com.hugo.video_service.videos.dto.VideoProgressRequest;
import com.hugo.video_service.videos.repositories.VideoProgressRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.mongodb.MongoDBAtlasLocalContainer;
import static org.assertj.core.api.Assertions.*;

@AutoConfigureMockMvc
@RequiredArgsConstructor
@SpringBootTest
public class VideoProgressIT {

    MongoDBAtlasLocalContainer mongoDBAtlasLocalContainer = ContainerUtils.setUpMongoAtlas();

    @Autowired
    private VideoProgressRepository videoProgressRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void clean(){
        videoProgressRepository.deleteAll();
    }

    @Test
    public void testIfVideoProgressCanBeCreated() throws Exception {
        VideoProgressRequest videoProgressRequest = VideoProgressRequest
                .builder()
                .lastPositionInSeconds(33.3333F)
                .build();

        String json = objectMapper.writeValueAsString(videoProgressRequest);

        mockMvc.perform(MockMvcRequestBuilders.patch("/videos/123/progress")
                .header("user_id", "123")
                .content(json)
                .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testIdVideoProgressCanBeRetrieved() throws Exception {
        VideoProgress videoProgress = VideoProgress.builder()
                .videoId("123")
                .userId("123")
                .lastPositionInSeconds(3.33F)
                .build();
        videoProgressRepository.save(videoProgress);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/videos/123/progress")
                .header("user_id", "123")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        VideoProgressDto videoProgressDto = objectMapper.readValue(result.getResponse().getContentAsString(), VideoProgressDto.class);

        assertThat(videoProgressDto.getVideoId()).isEqualTo("123");
        assertThat(videoProgressDto.getUserId()).isEqualTo("123");
    }


}
