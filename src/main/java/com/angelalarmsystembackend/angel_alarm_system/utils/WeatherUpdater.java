package com.angelalarmsystembackend.angel_alarm_system.utils;

import com.angelalarmsystembackend.angel_alarm_system.client.WeatherClient;
import com.angelalarmsystembackend.angel_alarm_system.model.DeviceClientData;
import com.angelalarmsystembackend.angel_alarm_system.model.OpenWeatherData;
import com.angelalarmsystembackend.angel_alarm_system.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WeatherUpdater {

    private final WeatherService weatherService;
    @Autowired
    private final WeatherClient weatherClient;
    public WeatherUpdater(WeatherService weatherService, WeatherClient weatherClient) {
        this.weatherService = weatherService;
        this.weatherClient = weatherClient;
    }

    @Scheduled(initialDelay = 0, fixedRate = 3600000)
    public void updateWeather() throws IOException, InterruptedException {
        OpenWeatherData openWeatherData = weatherClient.getWeather();
        System.out.println(openWeatherData);
        weatherService.setWeather(openWeatherData);
    }
}
