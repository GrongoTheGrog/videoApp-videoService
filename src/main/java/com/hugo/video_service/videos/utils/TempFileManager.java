package com.hugo.video_service.videos.utils;

import com.hugo.video_service.videos.exceptions.VideoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;

public class TempFileManager {

    private static final Logger log = LogManager.getLogger(TempFileManager.class);

    public static Path createFile(String suffix) {
        try{
            createBaseFolder();
            Path tempFile = Files.createFile(Path.of("videos", UUID.randomUUID().toString() + suffix));
            if (tempFile.toFile().exists()) tempFile.toFile().delete();
            tempFile.toFile().deleteOnExit();

            return tempFile;
        }catch (IOException e){
            log.error(e);
            throw new VideoException("Failed to create temp file.");
        }
    }

    public static Path createFolder(){
        try{
            createBaseFolder();
            Path basePath = Path.of("videos", UUID.randomUUID().toString());
            if (!basePath.toFile().exists()){
                Files.createDirectory(basePath);
            }

            basePath.toFile().deleteOnExit();

            return basePath;
        }catch (IOException e){
            log.error(e);
            throw new VideoException("Failed to create temp directory.");
        }
    }

    public static void deleteFolder(Path dir){
        try{
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }catch (IOException e){
            log.error(e);
            throw new VideoException("Failed to create temp directory.");
        }
    }

    public static void deleteFile(Path file){
        try{
            Files.delete(file);
        }catch (IOException e){
            log.error(e);
            throw new VideoException("Error deleting file.");
        }

    }

    public static void copyFromTo(InputStream inputStream, Path path){
        try{
            Files.copy(inputStream, path);
        }catch (IOException e){
            log.error(e);
            throw new VideoException("Error copying stream to file.");
        }
    }

    public static float getVideoDuration(Path path){
        try{
            ProcessBuilder pb = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1",
                    path.toString()
            );

            Process process = pb.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = bufferedReader.readLine();

            return Float.parseFloat(line);

        }catch (IOException e){
            log.error(e);
            throw new VideoException("Error retrieving video duration.");
        }

    }

    private static void createBaseFolder() throws IOException {
        Path dir = Path.of("videos");
        if (!dir.toFile().exists()){
            Files.createDirectory(Path.of("videos"));
        }
    }


}
