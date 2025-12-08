package com.angelalarmsystembackend.angel_alarm_system.controller;

/*
receive requests and validate user so send the requests to the machine server
 */
import com.angelalarmsystembackend.angel_alarm_system.model.AASData;
import com.angelalarmsystembackend.angel_alarm_system.model.DeviceData;
import com.angelalarmsystembackend.angel_alarm_system.model.SlideShowData;
import com.angelalarmsystembackend.angel_alarm_system.service.DeviceService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
public class AngelAlarmSystemController {
    @PostMapping("/connect")
    public SlideShowData connect(@RequestBody AASData aasData) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException {
        return DeviceService.connectToDevice(aasData);
    }

     @PostMapping("/connectDevice")
     public void connectDevice(@RequestBody DeviceData data) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
         System.out.println("RECEIVED CONNECTION! FROM: " + data.getIpAddress() + " username: " + data.getDeviceName() + " password: " + data.getPassword());
         DeviceService.connectDevice(data);
     }
}
