package com.angelalarmsystembackend.angel_alarm_system.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherData {
    TemperatureData main;
    List<WeatherData> weather;
}
