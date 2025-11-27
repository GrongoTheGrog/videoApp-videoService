package com.hugo.video_service.comments;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document("replies")
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class Reply extends Comment{

    private String commentId;

}
