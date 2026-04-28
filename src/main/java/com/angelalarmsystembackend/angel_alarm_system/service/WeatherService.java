package com.angelalarmsystembackend.angel_alarm_system.service;

import com.angelalarmsystembackend.angel_alarm_system.client.WeatherClient;
import com.angelalarmsystembackend.angel_alarm_system.model.*;
import com.angelalarmsystembackend.angel_alarm_system.model.WeatherGov.WeatherGovData;
import com.angelalarmsystembackend.angel_alarm_system.utils.WeatherUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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

    public void setWeather(OpenWeatherData openWeatherData) throws IOException, InterruptedException {
        WeatherGovData weatherGovData = weatherClient.getWeather();
        WeatherGovData apparentTemperatures = weatherClient.getGridPoints();
        List<Integer> lowAndHighTemperature = WeatherUtils.getLowAndMaxTemperature(weatherGovData.getProperties().getPeriods());
        Integer feelsLike = WeatherUtils.getFeelsLikeTemperature(apparentTemperatures.getProperties().getApparentTemperature().getValues());
        WeatherService.openWeatherData = OpenWeatherData.builder()
                .main(
                        TemperatureData.builder()
                                .feels_like(feelsLike)
                                .temp(Integer.parseInt(weatherGovData.getProperties().getPeriods().get(0).getTemperature()))
                                .temp_min(lowAndHighTemperature.get(0))
                                .temp_max(lowAndHighTemperature.get(1)).build()
                ).weather(
                    List.of(
                            WeatherData.builder()
                                    .id(openWeatherData.getWeather().get(0).getId())
                                    .description(openWeatherData.getWeather().get(0).getDescription())
                                    .icon(openWeatherData.getWeather().get(0).getIcon())
                                    .build()
                    )
                )
                .build();
    }
    public static OpenWeatherData getWeather(DeviceData aasData){
        System.out.println("getting weather data");
        System.out.println(aasData);
        if (aasData.getDeviceName().equalsIgnoreCase("weather-authorized-machine-1312") || DeviceService.deviceNameToDeviceClientData.get(aasData.getDeviceName()).getPassword().equalsIgnoreCase(aasData.getPassword())){
            System.out.println("is valid device, requested data: ");
            System.out.println(openWeatherData);
            return openWeatherData;
        }else{
            System.out.println("not a valid device.");
            return OpenWeatherData.builder().build();
        }
    }
}
