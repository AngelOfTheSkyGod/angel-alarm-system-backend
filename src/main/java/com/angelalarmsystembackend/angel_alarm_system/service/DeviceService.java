package com.angelalarmsystembackend.angel_alarm_system.service;

import com.angelalarmsystembackend.angel_alarm_system.client.DeviceClient;
import com.angelalarmsystembackend.angel_alarm_system.model.*;
import com.angelalarmsystembackend.angel_alarm_system.utils.AccountUtils;
import com.angelalarmsystembackend.angel_alarm_system.utils.ImageUtils;
import com.angelalarmsystembackend.angel_alarm_system.utils.IpAddressUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import static com.angelalarmsystembackend.angel_alarm_system.constants.AngelAlarmSystemConstants.PAGE_SIZE;

public class DeviceService {
    public static final Set<DeviceData> devices = new HashSet<>();
    public static final Map<String, DeviceClientData> deviceNameToDeviceClientData = new HashMap<>();
    public static final Map<String, DeviceClientData> clientToMachineMap = new HashMap<String, DeviceClientData>();

    public static void connectDevice(DeviceData deviceData) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (devices.contains(deviceData) || IpAddressUtils.isLocalIpAddress(deviceData.getIpAddress())){
            System.err.println("is not a local ip address or device is already in the system");
            return;
        }
        String[] saltAndHash = AccountUtils.storePassword(deviceData.getPassword());
        devices.add(deviceData);
        deviceNameToDeviceClientData.put(deviceData.getDeviceName(), DeviceClientData.builder()
                .deviceName(deviceData.getDeviceName())
                .ipAddress(deviceData.getIpAddress())
                .password(deviceData.getPassword())
                .salt(saltAndHash[0])
                .passwordHash(saltAndHash[1])
                .build());
    }

    public static SlideShowData connectSlideShow(SlideShowRequest slideShowRequest) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InterruptedException {
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
        Integer numberOfPages = numberOfImages / PAGE_SIZE;

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
        if (!AccountUtils.isAuthenticated(aasData.getUserIdentifier(), aasData.getUsername(), aasData.getPassword())){
            return null;
        }
        DeviceClientData deviceClient = deviceNameToDeviceClientData.get(aasData.getUsername());
        clientToMachineMap.put(aasData.getUserIdentifier(), deviceClient);
        return DeviceClient.sendConnect(deviceClient.getIpAddress());
    }


    public static ImageRequestResponse addImage(AddImageRequest addImageRequest) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InterruptedException {
        if (!AccountUtils.isAuthenticated(addImageRequest.getUserIdentifier(), addImageRequest.getUsername(), addImageRequest.getPassword())){
            return null;
        }
        boolean success = ImageUtils.makeImage(addImageRequest.getImageDataUrl(), "/data/images/" + clientToMachineMap.get(addImageRequest.getUserIdentifier()).getDeviceName() + "/" + addImageRequest.getFileName());
        Integer numberOfImages = ImageUtils.countFiles("/data/images/" + clientToMachineMap.get(addImageRequest.getUserIdentifier()).getDeviceName());
        Integer numberOfPages = numberOfImages / PAGE_SIZE;
        return ImageRequestResponse.builder().imageCount(numberOfImages).numberOfPages(numberOfPages).success(success).build();
    }

    public static ImageRequestResponse deleteImage(DeleteImageRequest deleteImageRequest) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InterruptedException {
        if (!AccountUtils.isAuthenticated(deleteImageRequest.getUserIdentifier(), deleteImageRequest.getUsername(), deleteImageRequest.getPassword())){
            return null;
        }
        String imagePath = "/data/images/" + clientToMachineMap.get(deleteImageRequest.getUserIdentifier()).getDeviceName();
        boolean success = ImageUtils.deleteFilesByIndexes(imagePath, deleteImageRequest.getPageNumber(), deleteImageRequest.getImagesDeleted());
        Integer numberOfImages = ImageUtils.countFiles("/data/images/" + clientToMachineMap.get(deleteImageRequest.getUserIdentifier()).getDeviceName());
        Integer numberOfPages = numberOfImages / PAGE_SIZE;
        return ImageRequestResponse.builder().imageCount(numberOfImages).numberOfPages(numberOfPages).success(success).build();
    }
}
