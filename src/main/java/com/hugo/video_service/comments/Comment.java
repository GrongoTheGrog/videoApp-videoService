package com.hugo.video_service.comments;

import com.hugo.video_service.common.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Comment {

    @Id
    private String id;
    private User user;
    private String videoId;
    private String content;

}
