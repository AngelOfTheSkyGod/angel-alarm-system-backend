package com.angelalarmsystembackend.angel_alarm_system.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DeviceData {
    private String deviceName;
    private String password;
    private String ipAddress;
    private String passwordHash;
    private String salt;
}
