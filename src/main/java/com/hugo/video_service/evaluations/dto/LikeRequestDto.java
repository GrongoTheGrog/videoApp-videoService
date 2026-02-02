package com.hugo.video_service.evaluations.dto;

import com.hugo.video_service.evaluations.EvaluationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeRequestDto {
    EvaluationType evaluationType;
}
