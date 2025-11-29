package com.hugo.video_service;

import com.hugo.video_service.comments.Comment;
import com.hugo.video_service.comments.Reply;
import com.hugo.video_service.videos.Video;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtils {

    public static MockMultipartFile getTestVideo() throws IOException {
        Path videoPath = Path.of("src", "test", "resources", "test-video.mp4");
        InputStream inputStream = Files.newInputStream(videoPath);
        return new MockMultipartFile("file", "test-video.mp4", "video/mp4", inputStream);
    }

    public static Video getVideo(){
        return Video.builder()
                .title("title")
                .views(111L)
                .description("description")
                .durationInSeconds(34.2f)
                .userId("123")
                .build();
    }

    public static Comment getComment(){
        return Comment.builder()
                .content("Content content content.")
                .videoId("1234")
                .userId("1234")
                .build();
    }

    public static Reply getReply(){
        return Reply.builder()
                .content("Content content content.")
                .videoId("1234")
                .userId("1234")
                .commentId("1234")
                .build();
    }
}
