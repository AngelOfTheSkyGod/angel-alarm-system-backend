package com.angelalarmsystembackend.angel_alarm_system.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppPropertiesAccessor {

    @Value("${weather.key}")
    private String weatherKey;

    public String getWeatherKey() {
        return weatherKey;
    }
}