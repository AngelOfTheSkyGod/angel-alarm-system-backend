package com.angelalarmsystembackend.angel_alarm_system.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemperatureData {
    int temp;
    int temp_min;
    int temp_max;
    int feels_like;
}
