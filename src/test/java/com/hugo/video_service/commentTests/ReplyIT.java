package com.hugo.video_service.commentTests;


import com.hugo.video_service.ContainerUtils;
import com.hugo.video_service.TestUtils;
import com.hugo.video_service.comments.Reply;
import com.hugo.video_service.comments.dto.CreateReplyDto;
import com.hugo.video_service.comments.repositories.ReplyRepository;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@AutoConfigureMockMvc
@SpringBootTest
public class ReplyIT {

    private static final MongoDBAtlasLocalContainer mongoDbContainer = ContainerUtils.setUpMongoAtlas();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    ReplyRepository replyRepository;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    public void clean(){
        replyRepository.deleteAll();
    }

    @Test
    public void testIfReplyCanBeCreated() throws Exception {

        CreateReplyDto createReplyDto = CreateReplyDto.builder()
                .commentId("123")
                .content("reply content")
                .videoId("123")
                .build();

        String replyJson = objectMapper.writeValueAsString(createReplyDto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/replies")
                .contentType("application/json")
                .header("user_id", "123")
                .content(replyJson)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        Reply reply = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Reply.class);

        assertThat(reply.getContent()).isEqualTo(createReplyDto.getContent());
        assertThat(reply.getVideoId()).isEqualTo(createReplyDto.getVideoId());
        assertThat(reply.getCommentId()).isEqualTo(createReplyDto.getCommentId());
        assertThat(reply.getUserId()).isEqualTo("123");

    }


    @Test
    public void testIfReplyCanBeRetrievedByCommentId() throws Exception {
        Reply reply1 = TestUtils.getReply();
        Reply reply2 = TestUtils.getReply();

        replyRepository.save(reply1);
        replyRepository.save(reply2);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/comments/" + reply2.getCommentId() + "/replies")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Map<String, Object> page = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        List<Reply> replies = (List<Reply>) page.get("content");

        assertThat(replies.size()).isEqualTo(2);
    }

    @Test
    public void testIfPaginationIsWorking() throws Exception {
        Reply reply1 = TestUtils.getReply();
        Reply reply2 = TestUtils.getReply();

        replyRepository.save(reply1);
        replyRepository.save(reply2);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/comments/" + reply2.getCommentId() + "/replies?size=1")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Map<String, Object> page = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        List<Map<String, String>> replies = (List<Map<String, String>>) page.get("content");

        assertThat(replies.size()).isEqualTo(1);

        MvcResult result2 = mockMvc.perform(
                        MockMvcRequestBuilders.get("/comments/" + reply2.getCommentId() + "/replies?size=1&page=1")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Map<String, Object> page2 = objectMapper.readValue(result2.getResponse().getContentAsString(), Map.class);
        List<Map<String, String>> replies2 = (List<Map<String, String>>) page2.get("content");

        assertThat(replies2.size()).isEqualTo(1);

        assertThat(replies2.getFirst().get("id")).isNotEqualTo(replies.getFirst().get("id"));
    }


}
