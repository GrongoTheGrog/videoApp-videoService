package com.hugo.video_service.service;

import com.hugo.video_service.exceptions.VideoException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCustomPolicy;
import software.amazon.awssdk.services.cloudfront.model.CustomSignerRequest;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CloudfrontService {

    @Value("${aws.cloudfront.key-pair-id}")
    private String keyPairId;

    @Value("${aws.cloudfront.path-to-private-key}")
    private String pathToKey;

    @Value("${aws.cloudfront.domain}")
    private String domain;

    private final CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();

    public List<String> getCookieHeaders(String pathToStreamFolder, LocalDateTime expiresAt){

        try{
            CustomSignerRequest customRequest = CustomSignerRequest.builder()
                    .resourceUrl(pathToStreamFolder)
                    .privateKey(Path.of(pathToKey))
                    .keyPairId(keyPairId)
                    .expirationDate(expiresAt.toInstant(OffsetDateTime.now().getOffset()))
                    .activeDate(OffsetDateTime.now().toInstant())
                    .build();

            CookiesForCustomPolicy cookiesForCustomPolicy = cloudFrontUtilities.getCookiesForCustomPolicy(
                    customRequest
            );

            List<String> cookies = new ArrayList<>();

            String extraFields = "; Path=/; Secure; Domain=" + domain;

            cookies.add(cookiesForCustomPolicy.signatureHeaderValue() + extraFields);
            cookies.add(cookiesForCustomPolicy.keyPairIdHeaderValue() + extraFields);
            cookies.add(cookiesForCustomPolicy.policyHeaderValue() + extraFields);

            return cookies;

        }catch (Exception e){
            throw new VideoException(e.getMessage());
        }
    }
}
