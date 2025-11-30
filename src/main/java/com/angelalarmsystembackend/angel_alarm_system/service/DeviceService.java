package com.angelalarmsystembackend.angel_alarm_system.service;

import com.angelalarmsystembackend.angel_alarm_system.client.DeviceClient;
import com.angelalarmsystembackend.angel_alarm_system.model.AASData;
import com.angelalarmsystembackend.angel_alarm_system.model.DeviceClientData;
import com.angelalarmsystembackend.angel_alarm_system.model.DeviceData;
import com.angelalarmsystembackend.angel_alarm_system.utils.AccountUtils;
import com.angelalarmsystembackend.angel_alarm_system.utils.IpAddressUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class DeviceService {
    private static final Set<DeviceData> devices = new HashSet<>();
    private static final Map<String, DeviceClientData> deviceNameToDeviceClientData = new HashMap<>();
    private static final Map<String, DeviceClientData> clientToMachineMap = new HashMap<String, DeviceClientData>();

    public static void connectDevice(DeviceData deviceData) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (!devices.contains(deviceData) || IpAddressUtils.isLocalIpAddress(deviceData.getIpAddress())){
            System.err.println("is not a local ip address or device is already in the system" + deviceData.getIpAddress());
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

    public static void connectToDevice(AASData aasData) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InterruptedException {
        if (clientToMachineMap.get(aasData.getRemoteAddr()) != null){
            System.err.println("user is connected to a device already.");
            return;
        }
        System.out.println("ip address of client: " + aasData.getRemoteAddr());
        DeviceClientData deviceClient = deviceNameToDeviceClientData.get(aasData.getUsername());
        if (deviceClient != null && AccountUtils.verifyPassword(aasData.getPassword(), deviceClient.getSalt(), deviceClient.getPasswordHash())){
            DeviceClient.sendPing(aasData.getRemoteAddr(), aasData);
            clientToMachineMap.put(aasData.getRemoteAddr(), deviceClient);
        }
    }
}
