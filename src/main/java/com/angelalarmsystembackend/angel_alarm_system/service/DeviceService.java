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
//        DeviceClientData deviceClient = deviceNameToDeviceClientData.get(slideShowRequest.getUsername());
//        System.out.println("device client: " + deviceClient.getIpAddress() + " name : " + deviceClient.getDeviceName());
//        clientToMachineMap.put(slideShowRequest.getUserIdentifier(), deviceClient);
        List<String> imageList = ImageUtils.getFilePaths("/data/images/" + clientToMachineMap.get(slideShowRequest.getUserIdentifier()).getDeviceName());
        Integer numberOfImages = ImageUtils.countFiles("/data/images/" + clientToMachineMap.get(slideShowRequest.getUserIdentifier()).getDeviceName());

        return SlideShowData.builder().imageList(
                imageList
        ).imageCount(numberOfImages).build();
    }

    public static AASData connectToDevice(AASData aasData) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InterruptedException {
        if (!AccountUtils.isAuthenticated(aasData.getUserIdentifier(), aasData.getUsername(), aasData.getPassword())){
            return null;
        }
        DeviceClientData deviceClient = deviceNameToDeviceClientData.get(aasData.getUsername());
        System.out.println("device client: " + deviceClient.getIpAddress() + " name : " + deviceClient.getDeviceName());
        clientToMachineMap.put(aasData.getUserIdentifier(), deviceClient);
        return DeviceClient.sendConnect(deviceClient.getIpAddress());
    }


    public static ImageRequestResponse addImage(AddImageRequest addImageRequest) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InterruptedException {
        if (!AccountUtils.isAuthenticated(addImageRequest.getUserIdentifier(), addImageRequest.getUsername(), addImageRequest.getPassword())){
            return null;
        }
        byte[] imageBytes = Base64.getDecoder().decode(addImageRequest.getImageDataUrl());

        Path deviceDir = Paths.get("/data/images/", clientToMachineMap.get(addImageRequest.getUserIdentifier()).getDeviceName());
        Files.createDirectories(deviceDir);

        Path filePath = deviceDir.resolve(addImageRequest.getFileName());
        Files.write(filePath, imageBytes);
//        DeviceClientData deviceClient = deviceNameToDeviceClientData.get(addImageRequest.getUsername());
        boolean success = ImageUtils.makeImage(addImageRequest.getImageDataUrl(), "/data/images/" + clientToMachineMap.get(addImageRequest.getUserIdentifier()).getDeviceName() + "/" + addImageRequest.getFileName());
        Integer numberOfImages = ImageUtils.countFiles("/data/images/" + clientToMachineMap.get(addImageRequest.getUserIdentifier()).getDeviceName());
        return ImageRequestResponse.builder().imageCount(numberOfImages).success(success).build();
    }

    public static ImageRequestResponse deleteImage(DeleteImageRequest deleteImageRequest) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InterruptedException {
        if (!AccountUtils.isAuthenticated(deleteImageRequest.getUserIdentifier(), deleteImageRequest.getUsername(), deleteImageRequest.getPassword())){
            return null;
        }
        DeviceClientData deviceClient = deviceNameToDeviceClientData.get(deleteImageRequest.getUsername());
        return DeviceClient.deleteImage(deviceClient.getIpAddress(), deleteImageRequest.getImagePosition());
    }
}
