package com.angelalarmsystembackend.angel_alarm_system.utils;
import com.angelalarmsystembackend.angel_alarm_system.model.DeleteImageRequestPi0;
import com.angelalarmsystembackend.angel_alarm_system.model.DeviceClientData;
import com.angelalarmsystembackend.angel_alarm_system.service.DeviceService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

import static com.angelalarmsystembackend.angel_alarm_system.constants.AngelAlarmSystemConstants.PAGE_SIZE;

public class ImageUtils {
    private static BufferedImage resizeKeepingAspectRatio(
            BufferedImage originalImage,
            int targetWidth,
            int targetHeight) {

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        double widthRatio = (double) targetWidth / originalWidth;
        double heightRatio = (double) targetHeight / originalHeight;

        double scale = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);
        System.out.println("height: " + targetHeight + " width: "+ targetWidth + " new height: " + newHeight + " new width: " + newWidth);
        BufferedImage resized =
                new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resized.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resized;
    }
    public static BufferedImage makeImage(String base64, String filePath, Integer width, Integer height) throws IOException {

        if (base64.contains(",")) {
            base64 = base64.split(",")[1];
        }
        byte[] imageBytes = Base64.getDecoder().decode(base64);
        BufferedImage originalImage =
                ImageIO.read(new ByteArrayInputStream(imageBytes));

        BufferedImage resizedImage =
                resizeKeepingAspectRatio(originalImage, width, height);

        // ✅ Always ensure .png extension
        if (!filePath.toLowerCase().endsWith(".png")) {
            filePath = filePath + ".png";
        }

        File outputFile = new File(filePath);
        outputFile.getParentFile().mkdirs(); // ensure folder exists
        System.out.println("resized image height: " + resizedImage.getHeight() + " resized image width: " + resizedImage.getWidth());
        if (!ImageIO.write(resizedImage, "png", outputFile)) {
            throw new IOException("Unsupported image format or corrupted image.");
        }
        System.out.println("created! file path: " + filePath);

        return resizedImage;
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

    public static boolean deleteFilesByIndexes(String folderPath, Integer page, int[] imagesDeleted, String username) {
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

            Arrays.sort(imagesDeleted);
            for (int i = imagesDeleted.length - 1; i >= 0; i--) {
                int index = imagesDeleted[i];

                if (index >= 0 && index < fileList.size()) {
                    Path fileToDelete = fileList.get(index);
                    DeviceClientData deviceClient = DeviceService.deviceNameToDeviceClientData.get(username.toLowerCase());
                    boolean isSuccess = Files.deleteIfExists(fileToDelete);
                    if (!isSuccess){
                        return false;
                    }
                    ImageDeleteQueue.enqueue(DeleteImageRequestPi0.builder()
                            .pathName("http://" + deviceClient.getIpAddress() + "/deleteImage")
                            .fileName(String.valueOf(fileToDelete.getFileName()))
                            .build());
                }
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Error deleting files", e);
        }
    }

    public static String bufferedImageToBase64(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}