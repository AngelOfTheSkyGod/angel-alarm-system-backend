package com.angelalarmsystembackend.angel_alarm_system.utils;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
public class ImageUtils {

    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Calculate the best new dimensions to fit within the target while maintaining aspect ratio
        float aspectRatio = (float) originalWidth / originalHeight;
        int newWidth = targetWidth;
        int newHeight = (int) (newWidth / aspectRatio);

        if (newHeight > targetHeight) {
            // If the calculated height exceeds the target height, recalculate based on the target height
            newHeight = targetHeight;
            newWidth = (int) (newHeight * aspectRatio);
        }

        // Create a new BufferedImage with the calculated dimensions and type
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());

        // Get Graphics2D object to draw the scaled image
        Graphics2D graphics = resizedImage.createGraphics();

        // Set rendering hints for high quality
        graphics.setRenderingHints(
                new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));

        // Draw the original image onto the new image, scaling it to the new dimensions
        graphics.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        graphics.dispose();

        return resizedImage;
    }

    public static boolean makeImage(String base64, String filePath) throws IOException {

        if (base64.contains(",")) {
            base64 = base64.split(",")[1];
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64);

        BufferedImage originalImage =
                ImageIO.read(new ByteArrayInputStream(imageBytes));

        int targetWidth = 480;
        int targetHeight = 320;

        BufferedImage resizedImage =
                resizeImage(originalImage, targetWidth, targetHeight);

        // ✅ Always ensure .png extension
        if (!filePath.toLowerCase().endsWith(".png")) {
            filePath = filePath + ".png";
        }

        File outputFile = new File(filePath);
        outputFile.getParentFile().mkdirs(); // ensure folder exists

        if (!ImageIO.write(resizedImage, "png", outputFile)) {
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

    public static List<String> getFileNames(String folderPath) {
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
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Error reading directory", e);
        }
    }
}