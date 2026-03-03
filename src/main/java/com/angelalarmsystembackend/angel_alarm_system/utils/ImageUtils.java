package com.angelalarmsystembackend.angel_alarm_system.utils;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
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

    public static boolean makeImage(String base64, String filePath) throws IOException {
        // Remove data URI prefix if present
        if (base64.contains(",")) {
            base64 = base64.split(",")[1];
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64);

        BufferedImage originalImage =
                ImageIO.read(new ByteArrayInputStream(imageBytes));

        int targetWidth = 480;
        int targetHeight = 320;

        BufferedImage resizedImage =
                resizeKeepingAspectRatio(originalImage, targetWidth, targetHeight);
        try{
            if (!ImageIO.write(resizedImage, "png", new File(filePath))){
               throw new IOException("Unsupported image format or corrupted image.");
            }
            return true;
        }catch(IOException e){
            return false;
        }
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

    public static List<String> getFilePaths(String folderPath) {
        try (Stream<Path> files = Files.list(Paths.get(folderPath))) {
            return files
                    .filter(Files::isRegularFile)
                    .map(Path::toAbsolutePath)   // optional but recommended
                    .map(Path::toString)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Error reading directory", e);
        }
    }
}