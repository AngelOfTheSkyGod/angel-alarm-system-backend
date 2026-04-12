package com.angelalarmsystembackend.angel_alarm_system.model.WeatherGov;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Properties {
    String units;
    List<Period> periods;
    ApparentTemperature apparentTemperature;
}
