package com.angelalarmsystembackend.angel_alarm_system.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class WeatherData {
    int id;
    String main;
    String description;
    String icon;
}
