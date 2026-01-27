package com.hugo.video_service.videos.utils;

import com.hugo.video_service.videos.exceptions.KeyException;
import lombok.extern.log4j.Log4j2;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Log4j2
public class KeyLoader {
    public static PrivateKey loadPrivateKey(String pem) {
        String privateKeyContent = pem
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey key = kf.generatePrivate(keySpec);
            log.info("Private key generated successfully.");
            return key;
        }catch (Exception e){
            throw new KeyException("Error generating private key.");
        }

    }
}
