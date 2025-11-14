package com.hugo.video_service.utils;

public class S3PathBuilder {

    public static String buildPath(String userId, String videoId, String ...paths){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("/").append(userId).append("/").append(videoId);

        for (String extraPath : paths){
            stringBuilder.append("/").append(extraPath);
        }

        return stringBuilder.toString();
    }
}
