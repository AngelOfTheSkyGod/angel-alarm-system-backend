package com.angelalarmsystembackend.angel_alarm_system.controller;

/*
receive requests and validate user so send the requests to the machine server
 */
import com.angelalarmsystembackend.angel_alarm_system.controller.model.AASData;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class AngelAlarmSystemController {
    @PostMapping("/connect")
    public void connect(@RequestBody AASData aasData) throws IOException {
        System.out.println(aasData.getAlarmData() + "\n" + aasData.getCalendarData() + aasData.getUsername() + aasData.getPassword());
    }
}