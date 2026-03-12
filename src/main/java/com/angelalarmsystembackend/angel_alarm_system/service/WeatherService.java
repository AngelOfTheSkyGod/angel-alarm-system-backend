package com.angelalarmsystembackend.angel_alarm_system.service;

import com.angelalarmsystembackend.angel_alarm_system.client.WeatherClient;
import com.angelalarmsystembackend.angel_alarm_system.model.AASData;
import com.angelalarmsystembackend.angel_alarm_system.model.OpenWeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class WeatherService {

    @Autowired
    WeatherClient weatherClient;
    public static OpenWeatherData openWeatherData;

    public WeatherService(WeatherClient weatherClient){
        if (openWeatherData == null){
            openWeatherData = new OpenWeatherData();
        }
        this.weatherClient = weatherClient;
    }

    public void setWeather(OpenWeatherData openWeatherData){
        WeatherService.openWeatherData = openWeatherData;
    }
    public static OpenWeatherData getWeather(AASData aasData){
        System.out.println("getting weather data");
        System.out.println(aasData);
        if (DeviceService.deviceNameToDeviceClientData.get(aasData.getUsername()).getPassword().equalsIgnoreCase(aasData.getPassword())){
            System.out.println("is valid device, requested data: ");
            System.out.println(openWeatherData);
            return openWeatherData;
        }else{
            System.out.println("not a valid device.");
            return OpenWeatherData.builder().build();
        }
    }
}
