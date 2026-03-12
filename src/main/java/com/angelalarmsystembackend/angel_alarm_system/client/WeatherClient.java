package com.angelalarmsystembackend.angel_alarm_system.client;

import com.angelalarmsystembackend.angel_alarm_system.model.AASData;
import com.angelalarmsystembackend.angel_alarm_system.model.ImageRequestResponse;
import com.angelalarmsystembackend.angel_alarm_system.model.OpenWeatherData;
import com.angelalarmsystembackend.angel_alarm_system.model.SlideShowPictureData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
public class WeatherClient {
    @Autowired
    private Environment env;

    public WeatherClient(Environment env) {
        this.env = env;
    }

    public OpenWeatherData getWeather() throws IOException, InterruptedException {
        System.out.println("weather key: " + env.getProperty("weather.key"));
        String url = "https://api.openweathermap.org/data/2.5/weather?q=Chicago&units=imperial&appid=" + env.getProperty("weather.key");

        ObjectMapper objectMapper = new ObjectMapper();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(
                response.body(),
                OpenWeatherData.class
        );
    }
}
