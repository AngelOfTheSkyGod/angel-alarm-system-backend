package com.angelalarmsystembackend.angel_alarm_system.service;

import com.angelalarmsystembackend.angel_alarm_system.client.DeviceClient;
import com.angelalarmsystembackend.angel_alarm_system.model.*;
import com.angelalarmsystembackend.angel_alarm_system.utils.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.List;
import java.util.UUID;

import static com.angelalarmsystembackend.angel_alarm_system.constants.AngelAlarmSystemConstants.PAGE_SIZE;
import static com.angelalarmsystembackend.angel_alarm_system.constants.AngelAlarmSystemConstants.whiteListedDevices;

public class DeviceService {
    public static final Set<DeviceData> devices = new HashSet<>();
    public static final Map<String, DeviceClientData> deviceNameToDeviceClientData = new HashMap<>();
    public static final Map<String, DeviceClientData> clientToMachineMap = new HashMap<String, DeviceClientData>();

    public static void connectDevice(DeviceData deviceData) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InterruptedException {
        if (devices.contains(deviceData) || (IpAddressUtils.isLocalIpAddress(deviceData.getIpAddress()) && !AccountUtils.isWhiteListedDevice(deviceData))){
            System.err.println("is not a local ip address or device is already in the system");
            return;
        }
        System.out.println("connecting:");
        System.out.println(deviceData);
        String[] saltAndHash = AccountUtils.storePassword(deviceData.getPassword());
        devices.add(deviceData);
        Path imagePath = Paths.get("/data/images", deviceData.getDeviceName());
        System.out.println("image path: " + imagePath);
        deviceNameToDeviceClientData.put(deviceData.getDeviceName(), DeviceClientData.builder()
                .deviceName(deviceData.getDeviceName())
                .ipAddress(deviceData.getIpAddress())
                .password(deviceData.getPassword())
                .width(deviceData.getWidth())
                .height(deviceData.getHeight())
                .salt(saltAndHash[0])
                .passwordHash(saltAndHash[1])
                .build());
        if (Files.exists(imagePath)) {
            List<String> fileNames = ImageUtils.getFileNames(imagePath.toString(), -1);
            System.out.println("file names: " + fileNames);
            for (String fileName : fileNames) {
                try {
                    Path filePath = imagePath.resolve(fileName);
                    System.out.println("file path: " + filePath);
                    byte[] fileBytes = Files.readAllBytes(filePath);
                    String base64 = Base64.getEncoder().encodeToString(fileBytes);

                    ImageSendQueue.enqueue(AddImageRequestPi0.builder()
                            .pathName("http://" + deviceData.getIpAddress() + "/addImage")
                            .imageDataUrl(base64)
                            .fileName(fileName)
                            .build());
                } catch (Exception e) {
                    System.err.println("Failed to process image: " + fileName);
                    e.printStackTrace();
                }
            }
        }
    }

    public static SlideShowData connectSlideShow(SlideShowRequest slideShowRequest) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InterruptedException {
        System.out.println("username: " + slideShowRequest.getUsername());
        System.out.println("uid: " + slideShowRequest.getUserIdentifier());
        if (!AccountUtils.isAuthenticated(slideShowRequest.getUserIdentifier(), slideShowRequest.getUsername(), slideShowRequest.getPassword())){
            return null;
        }
        String imagePath = "/data/images/" + clientToMachineMap.get(slideShowRequest.getUserIdentifier()).getDeviceName();
        Path path = Paths.get(imagePath);

        if (!Files.exists(path)) {
            Files.createDirectories(path);
            return SlideShowData.builder().imageList(List.of()).imageCount(0).build();
        }
        List<String> imageList = ImageUtils.getFileNames(imagePath, slideShowRequest.getPageNumber());
        Integer numberOfImages = ImageUtils.countFiles(imagePath);
        Integer numberOfPages = (numberOfImages == 0 ? 0 : numberOfImages - 1) / PAGE_SIZE;

        String baseUrl = "http://quinonesangel.com:1312/images/" + slideShowRequest.getUsername() + "/";

        List<SlideShowPictureData> pictures = imageList.stream()
                .map(fileName -> SlideShowPictureData.builder()
                        .fileName(fileName)
                        .imageDataUrl(baseUrl + fileName)
                        .build())
                .toList();

        return SlideShowData.builder().imageList(pictures).numberOfPages(numberOfPages).imageCount(numberOfImages).build();
    }

    public static AASData connectToDevice(AASData aasData) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InterruptedException {
        System.out.println("connecting to: " + aasData.getUsername() + " from: " + aasData.getUserIdentifier());
        if (!AccountUtils.isAuthenticated(aasData.getUserIdentifier(), aasData.getUsername(), aasData.getPassword())){
            return null;
        }
        DeviceClientData deviceClient = deviceNameToDeviceClientData.get(aasData.getUsername().toLowerCase());
        clientToMachineMap.put(aasData.getUserIdentifier(), deviceClient);
        return DeviceClient.sendConnect(deviceClient.getIpAddress());
    }


    public static ImageRequestResponse addImage(AddImageRequest addImageRequest) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InterruptedException {
        System.out.println("username: " + addImageRequest.getUsername());
        System.out.println("uid: " + addImageRequest.getUserIdentifier());
        System.out.println("image name: " + addImageRequest.getFileName());
        if (!AccountUtils.isAuthenticated(addImageRequest.getUserIdentifier(), addImageRequest.getUsername(), addImageRequest.getPassword())){
            return null;
        }
        String fileName = addImageRequest.getFileName();
        Path filePath = Path.of("/data/images/" + clientToMachineMap.get(addImageRequest.getUserIdentifier()).getDeviceName() + "/" + fileName + ".png");
        boolean exists = Files.exists(filePath);
        if (exists){
            fileName = fileName + UUID.randomUUID();
        }
        System.out.println("file name: " + fileName + " adding ");
        DeviceClientData deviceClientData = clientToMachineMap.get(addImageRequest.getUserIdentifier());
        BufferedImage image = ImageUtils.makeImage(addImageRequest.getImageDataUrl(), "/data/images/" + deviceClientData.getDeviceName() + "/" + fileName, deviceClientData.getWidth(), deviceClientData.getHeight());
        Integer numberOfImages = ImageUtils.countFiles("/data/images/" + clientToMachineMap.get(addImageRequest.getUserIdentifier()).getDeviceName());
        Integer numberOfPages = (numberOfImages == 0 ? 0 : numberOfImages - 1) / PAGE_SIZE;
        DeviceClientData deviceClient = deviceNameToDeviceClientData.get(addImageRequest.getUsername().toLowerCase());
        String base64Image = ImageUtils.bufferedImageToBase64(image, "png");
        ImageSendQueue.enqueue(AddImageRequestPi0.builder()
                        .pathName("http://" + deviceClient.getIpAddress() + "/addImage")
                        .imageDataUrl(base64Image)
                        .fileName(fileName)
                .build());
        return ImageRequestResponse.builder().imageCount(numberOfImages).numberOfPages(numberOfPages).success(true).build();
    }

    public static ImageRequestResponse deleteImage(DeleteImageRequest deleteImageRequest) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InterruptedException {
        if (!AccountUtils.isAuthenticated(deleteImageRequest.getUserIdentifier(), deleteImageRequest.getUsername(), deleteImageRequest.getPassword())){
            return null;
        }
        String imagePath = "/data/images/" + clientToMachineMap.get(deleteImageRequest.getUserIdentifier()).getDeviceName();
        boolean success = ImageUtils.deleteFilesByIndexes(imagePath, deleteImageRequest.getPageNumber(), deleteImageRequest.getImagesDeleted(), deleteImageRequest.getUsername());
        Integer numberOfImages = ImageUtils.countFiles("/data/images/" + clientToMachineMap.get(deleteImageRequest.getUserIdentifier()).getDeviceName());
        Integer numberOfPages = (numberOfImages == 0 ? 0 : numberOfImages - 1) / PAGE_SIZE;
        return ImageRequestResponse.builder().imageCount(numberOfImages).numberOfPages(numberOfPages).success(success).build();
    }
}
