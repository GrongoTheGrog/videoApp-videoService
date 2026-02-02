package com.hugo.video_service.evaluations.services;

import com.hugo.video_service.common.exceptions.HttpException;
import com.hugo.video_service.evaluations.Evaluation;
import com.hugo.video_service.evaluations.EvaluationType;
import com.hugo.video_service.evaluations.dto.LikeRequestDto;
import com.hugo.video_service.evaluations.repositories.EvaluationRepository;
import com.hugo.video_service.videos.Video;
import com.hugo.video_service.videos.repositories.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final VideoRepository videoRepository;

    public void checkAndPersistLike(LikeRequestDto requestDto, String userId, Video video){
        Optional<Evaluation> foundEvaluation = evaluationRepository.findByUserIdAndVideoId(
                userId,
                video.getId()
        );

        Evaluation evaluation = Evaluation.builder()
                .userId(userId)
                .videoId(video.getId())
                .evaluationType(requestDto.getEvaluationType())
                .build();;

        if (foundEvaluation.isPresent()){
            EvaluationType newEval = requestDto.getEvaluationType();
            EvaluationType oldEval = foundEvaluation.get().getEvaluationType();

            if (newEval == oldEval){
                throw new HttpException("Evaluation conflict.", HttpStatus.CONFLICT);
            }

            evaluation.setId(foundEvaluation.get().getId());
            video.applyEvaluation(oldEval, newEval);
        }else{
            video.incrementEvaluation(requestDto.getEvaluationType());
        }

        evaluationRepository.save(evaluation);
        videoRepository.save(video);
    }



}
