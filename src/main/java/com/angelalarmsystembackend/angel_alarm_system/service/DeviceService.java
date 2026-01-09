package com.angelalarmsystembackend.angel_alarm_system.service;

import com.angelalarmsystembackend.angel_alarm_system.client.DeviceClient;
import com.angelalarmsystembackend.angel_alarm_system.model.*;
import com.angelalarmsystembackend.angel_alarm_system.utils.AccountUtils;
import com.angelalarmsystembackend.angel_alarm_system.utils.IpAddressUtils;

import java.io.IOException;
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

    public static SlideShowData connectToDevice(AASData aasData) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InterruptedException {
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

        DeviceClientData deviceClient = deviceNameToDeviceClientData.get(addImageRequest.getUsername());
        return DeviceClient.addImage(deviceClient.getIpAddress(), addImageRequest.getImageDataUrl());
    }

    public static ImageRequestResponse deleteImage(DeleteImageRequest deleteImageRequest) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InterruptedException {
        if (!AccountUtils.isAuthenticated(deleteImageRequest.getUserIdentifier(), deleteImageRequest.getUsername(), deleteImageRequest.getPassword())){
            return null;
        }
        DeviceClientData deviceClient = deviceNameToDeviceClientData.get(deleteImageRequest.getUsername());
        return DeviceClient.deleteImage(deviceClient.getIpAddress(), deleteImageRequest.getImagePosition());
    }
}
