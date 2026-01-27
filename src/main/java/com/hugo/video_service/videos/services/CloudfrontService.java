package com.hugo.video_service.videos.services;

import com.hugo.video_service.videos.exceptions.VideoException;
import com.hugo.video_service.videos.utils.KeyLoader;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCustomPolicy;
import software.amazon.awssdk.services.cloudfront.model.CustomSignerRequest;

import java.nio.file.Path;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CloudfrontService {

    @Value("${aws.cloudfront.key-pair-id}")
    private String keyPairId;

    @Value("${aws.cloudfront.private-key}")
    private String privateKeyString;
    private PrivateKey privateKey;

    @Value("${aws.cloudfront.domain}")
    private String domain;

    private final CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();

    @PostConstruct
    public void init(){
        privateKey = KeyLoader.loadPrivateKey(privateKeyString);
    }

    public List<String> getCookieHeaders(String pathToStreamFolder, LocalDateTime expiresAt){

        try{
            CustomSignerRequest customRequest = CustomSignerRequest.builder()
                    .resourceUrl(pathToStreamFolder)
                    .privateKey(privateKey)
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
