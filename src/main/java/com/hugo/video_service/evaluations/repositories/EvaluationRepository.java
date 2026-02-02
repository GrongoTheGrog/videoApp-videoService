package com.hugo.video_service.evaluations.repositories;

import com.hugo.video_service.evaluations.Evaluation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluationRepository extends MongoRepository<Evaluation, String> {
    Optional<Evaluation> findByUserIdAndVideoId(
            String userId,
            String videoId
    );
}
