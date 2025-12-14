package com.angelalarmsystembackend.angel_alarm_system.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginDetails {
    private String username;
    private String password;
    private String userIdentifier;
}
