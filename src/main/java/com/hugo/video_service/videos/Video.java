package com.hugo.video_service.videos;


import com.hugo.video_service.evaluations.EvaluationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

@Repository
@Document("videos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Video {

    @Id
    private String id;
    private String userId;
    private Long sizeInBytes;
    private String title;
    private String thumbnailUrl;
    private Float durationInSeconds;
    private String description;
    private Long views;
    private int likes;
    private int dislikes;

    public void addViews(int views){
        this.views += views;
    }
    public void applyEvaluation(EvaluationType oldEval, EvaluationType newEval){
        if (newEval == EvaluationType.LIKE){
            this.likes++;
        }else if (newEval == EvaluationType.DISLIKE){
            this.dislikes++;
        }

        if (oldEval == EvaluationType.DISLIKE && newEval != EvaluationType.DISLIKE){
            this.dislikes--;
        }else if (oldEval == EvaluationType.LIKE && newEval != EvaluationType.LIKE){
            this.likes--;
        }
    }

    public void incrementEvaluation(EvaluationType eval){
        if (eval == EvaluationType.DISLIKE){
            dislikes++;
        }else if (eval == EvaluationType.LIKE){
            likes++;
        }
    }
}

