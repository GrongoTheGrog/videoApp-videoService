package com.hugo.video_service.commentTests;


import com.hugo.video_service.ContainerUtils;
import com.hugo.video_service.TestUtils;
import com.hugo.video_service.comments.Comment;
import com.hugo.video_service.comments.dto.CreateCommentDto;
import com.hugo.video_service.comments.dto.UpdateCommentReply;
import com.hugo.video_service.comments.repositories.CommentRepository;
import com.hugo.video_service.videos.repositories.VideoRepository;
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

import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.yaml.snakeyaml.events.CommentEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AutoConfigureMockMvc
@SpringBootTest
public class CommentIT {

    private static final MongoDBAtlasLocalContainer mongoDbContainer = ContainerUtils.setUpMongoAtlas();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testIfCommentCanBeCreated() throws Exception {

        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .videoId("123")
                .content("Really nice content here.")
                .build();

        String commentJson = objectMapper.writeValueAsString(createCommentDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/comments")
                .contentType("application/json")
                .content(commentJson)
                .header("user_id", "123")
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        Comment comment = objectMapper.readValue(result.getResponse().getContentAsString(), Comment.class);

        assertThat(comment.getContent()).isEqualTo(createCommentDto.getContent());
        assertThat(comment.getVideoId()).isEqualTo(createCommentDto.getVideoId());
        assertThat(comment.getUserId()).isEqualTo("123");

        Optional<Comment> createdComment = commentRepository.findById(comment.getId());

        assertThat(createdComment.isPresent()).isTrue();
    }

    @Test
    public void testIfCommentCanBeRetrievedByVideoId() throws Exception {

        Comment comment = TestUtils.getComment();
        commentRepository.save(comment);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/videos/" + comment.getVideoId() + "/comments")
                        .header("user_id", "123")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Map<String, Object> page = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        List<Map<String, String>> comments = (List<Map<String, String>>) page.get("content");

        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.getFirst().get("id")).isEqualTo(comment.getId());
    }


    @Test
    public void testIfCommentCanBeDeletedByOwner() throws Exception {

        Comment comment = TestUtils.getComment();
        commentRepository.save(comment);

        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/" + comment.getId())
                .header("user_id", comment.getUserId())
                .header("user_roles", List.of("USER"))
        ).andExpect(MockMvcResultMatchers.status().isNoContent());

        Optional<Comment> deletedComment = commentRepository.findById(comment.getId());

        assertThat(deletedComment.isEmpty()).isTrue();
    }

    @Test
    public void testIfCommentCanBeDeletedByAdmin() throws Exception {

        Comment comment = TestUtils.getComment();
        commentRepository.save(comment);

        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/" + comment.getId())
                .header("user_id", "admin")
                .header("user_roles", List.of("ADMIN", "USER"))
        ).andExpect(MockMvcResultMatchers.status().isNoContent());

        Optional<Comment> deletedComment = commentRepository.findById(comment.getId());

        assertThat(deletedComment.isEmpty()).isTrue();
    }


    @Test
    public void testIfCommentCannotBeDeletedByAnotherUser() throws Exception {

        Comment comment = TestUtils.getComment();
        commentRepository.save(comment);

        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/" + comment.getId())
                .header("user_id", "another_user_id")
                .header("user_roles", List.of("USER"))
        ).andExpect(MockMvcResultMatchers.status().isForbidden());

        Optional<Comment> deletedComment = commentRepository.findById(comment.getId());

        assertThat(deletedComment.isPresent()).isTrue();
    }

    @Test
    public void testIfContentOfCommentCanBeUpdated() throws Exception {

        Comment comment = TestUtils.getComment();
        commentRepository.save(comment);

        UpdateCommentReply updateCommentReply = UpdateCommentReply.builder()
                        .content("completely new content")
                        .build();

        String json = objectMapper.writeValueAsString(updateCommentReply);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/comments/" + comment.getId())
                .header("user_id", comment.getUserId())
                .contentType("application/json")
                .content(json)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Comment responseComment = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Comment.class);
        assertThat(responseComment.getContent()).isEqualTo(updateCommentReply.getContent());
    }
}
