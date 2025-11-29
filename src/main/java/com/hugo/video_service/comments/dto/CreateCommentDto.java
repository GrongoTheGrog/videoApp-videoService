package com.hugo.video_service.comments.dto;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class CreateCommentDto {

    private String videoId;

    @Size(max = 200, message = "Comment can't have more than 200 characters.")
    private String content;
}
