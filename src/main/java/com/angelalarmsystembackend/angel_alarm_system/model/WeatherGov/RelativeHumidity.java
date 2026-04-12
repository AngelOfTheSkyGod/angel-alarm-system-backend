package com.angelalarmsystembackend.angel_alarm_system.model.WeatherGov;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelativeHumidity {
    String value;
}
