package com.angelalarmsystembackend.angel_alarm_system.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginDetails {
    private String username;
    private String password;
    private String userIdentifier;
}
