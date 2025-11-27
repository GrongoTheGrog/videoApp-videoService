package com.hugo.video_service.videos.services;


import com.hugo.video_service.common.exceptions.HttpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Log4j2
public class S3Service {

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;

    public void saveFile(Path file, String key) {
        try{

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
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

    public void deleteVideoByIdAndUserId(String fileId, String userId){
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                .prefix(userId + "/" + fileId)
                .bucket(bucketName)
                .build();

        ListObjectsResponse res = null;

        while(res == null || !res.isTruncated()){
            res = s3Client.listObjects(listObjectsRequest);

            if (!res.contents().isEmpty()){
                DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                        .bucket(bucketName)
                        .delete(Delete.builder()
                                .objects(res.contents().stream()
                                        .map(
                                        s3Object -> ObjectIdentifier.builder()
                                                .key(s3Object.key())
                                                .build()
                                        ).toList()
                                ).build()
                        ).build();

                DeleteObjectsResponse deleteObjectsResponse = s3Client.deleteObjects(deleteObjectsRequest);
            }
        }
    }

    private void copyFromToFile(MultipartFile multipartFile, Path path) throws IOException {
        InputStream inputStream = multipartFile.getInputStream();
        Files.copy(inputStream, path);
    }

}
