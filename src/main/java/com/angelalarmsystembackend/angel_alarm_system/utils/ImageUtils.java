package com.angelalarmsystembackend.angel_alarm_system.utils;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

import static com.angelalarmsystembackend.angel_alarm_system.constants.AngelAlarmSystemConstants.PAGE_SIZE;

public class ImageUtils {

    public static boolean makeImage(String base64, String filePath) throws IOException {

        if (base64.contains(",")) {
            base64 = base64.split(",")[1];
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64);

        BufferedImage originalImage =
                ImageIO.read(new ByteArrayInputStream(imageBytes));

        // ✅ Always ensure .png extension
        if (!filePath.toLowerCase().endsWith(".png")) {
            filePath = filePath + ".png";
        }

        File outputFile = new File(filePath);
        outputFile.getParentFile().mkdirs(); // ensure folder exists

        if (!ImageIO.write(originalImage, "png", outputFile)) {
            throw new IOException("Unsupported image format or corrupted image.");
        }

        return true;
    }

    public static Integer countFiles(String folderPath) {
        try (Stream<Path> files = Files.list(Paths.get(folderPath))) {
            return Math.toIntExact(files
                    .filter(Files::isRegularFile)
                    .count());
        } catch (IOException e) {
            throw new RuntimeException("Error reading directory", e);
        }
    }

    public static List<String> getFileNames(String folderPath, Integer page) {
        try (Stream<Path> files = Files.list(Paths.get(folderPath))) {
            return files
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparingLong(path -> {
                        try {
                            return Files.getLastModifiedTime((Path) path).toMillis();
                        } catch (IOException e) {
                            return 0L;
                        }
                    }))
                    .skip(((long) page) * PAGE_SIZE)
                    .limit(PAGE_SIZE)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Error reading directory", e);
        }
    }

    public static boolean deleteFilesByIndexes(String folderPath, Integer page, int[] imagesDeleted) {
        if (page < 0 || page > countFiles(folderPath) / PAGE_SIZE || imagesDeleted.length > PAGE_SIZE || imagesDeleted.length == 0) {
            return false;
        }
        try (Stream<Path> files = Files.list(Paths.get(folderPath))) {

            List<Path> fileList = files
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparingLong(path -> {
                        try {
                            return Files.getLastModifiedTime(path).toMillis();
                        } catch (IOException e) {
                            return 0L;
                        }
                    }))
                    .skip((long) page * PAGE_SIZE)
                    .limit(PAGE_SIZE)
                    .toList();

            List<String> deletedFiles = new ArrayList<>();

            Arrays.sort(imagesDeleted);
            for (int i = imagesDeleted.length - 1; i >= 0; i--) {
                int index = imagesDeleted[i];

                if (index >= 0 && index < fileList.size()) {
                    Path fileToDelete = fileList.get(index);
                    boolean isSuccess = Files.deleteIfExists(fileToDelete);
                    if (!isSuccess){
                        return false;
                    }
                    deletedFiles.add(fileToDelete.getFileName().toString());
                }
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Error deleting files", e);
        }
    }
}