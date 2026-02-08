package com.hugo.video_service.videos.services;


import com.hugo.video_service.common.exceptions.HttpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Log4j2
public class S3Service {

    @Value("${aws.s3.bucket.video.name}")
    private String videoBucketName;

    @Value("${aws.s3.bucket.thumbnail.host}")
    private String thumbnailBucketHost;

    private final S3Client s3Client;

    public void saveFile(Path file, String key) {
        try{

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(videoBucketName)
                    .key(key)
                    .contentType("video/mp4")
                    .build();

            log.info("Sending file {} to S3...", key);
            s3Client.putObject(putObjectRequest, file);
            log.info("File {} sent.", key);
        }catch (Exception e){
            throw new HttpException("Error persisting file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteVideoByIdAndUserId(String fileId, String userId) {
        String prefix = userId + "/" + fileId;

        String continuationToken = null;
        List<List<S3Object>> pages = new ArrayList<>();

        ListObjectsV2Response res = null;

        while (res == null || res.isTruncated()) {
            ListObjectsV2Request req = ListObjectsV2Request.builder()
                    .bucket(videoBucketName)
                    .prefix(prefix)
                    .continuationToken(continuationToken)
                    .build();

            res = s3Client.listObjectsV2(req);
            continuationToken = res.nextContinuationToken();

            if (!res.contents().isEmpty()) {
                pages.add(res.contents());
            }
        }

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        for (List<S3Object> page : pages) {
            executor.submit(() -> {

                List<ObjectIdentifier> keys = page.stream()
                        .map(o -> ObjectIdentifier.builder().key(o.key()).build())
                        .toList();

                DeleteObjectsRequest deleteReq =
                        DeleteObjectsRequest.builder()
                                .bucket(videoBucketName)
                                .delete(Delete.builder().objects(keys).build())
                                .build();

                s3Client.deleteObjects(deleteReq);
            });
        }

        executor.shutdown();

        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                throw new HttpException("Timeout deleting files from cloud.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpException("Interrupted while deleting files from cloud.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String getThumbnailUrl(String videoId){
        return thumbnailBucketHost + "/videoId";
    }

    private void copyFromToFile(MultipartFile multipartFile, Path path) throws IOException {
        InputStream inputStream = multipartFile.getInputStream();
        Files.copy(inputStream, path);
    }

}
