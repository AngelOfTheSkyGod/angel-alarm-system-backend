package com.angelalarmsystembackend.angel_alarm_system.utils;

import com.angelalarmsystembackend.angel_alarm_system.client.WeatherClient;
import com.angelalarmsystembackend.angel_alarm_system.model.DeviceClientData;
import com.angelalarmsystembackend.angel_alarm_system.model.OpenWeatherData;
import com.angelalarmsystembackend.angel_alarm_system.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WeatherUpdater {

    private final WeatherService weatherService;
    @Autowired
    private final WeatherClient weatherClient;
    @Autowired
    private Environment env;
    public WeatherUpdater(WeatherService weatherService, WeatherClient weatherClient, Environment env) {
        this.weatherService = weatherService;
        this.weatherClient = weatherClient;
        this.env = env;
    }

    @Scheduled(initialDelay = 0, fixedRate = 1800000)
    public void updateWeather() throws IOException, InterruptedException {
        OpenWeatherData openWeatherData = weatherClient.getOpenWeatherMapWeather("https://api.openweathermap.org/data/2.5/weather?q=Chicago&units=imperial&appid=" + env.getProperty("weather.key"));
        OpenWeatherData openWeatherDataElgin = weatherClient.getOpenWeatherMapWeather("https://api.openweathermap.org/data/2.5/weather?q=Elgin,US&units=imperial&lang=es&appid=" + env.getProperty("weather.key"));

        System.out.println(openWeatherData);
        weatherService.setWeather(openWeatherData, openWeatherDataElgin);
    }
}
