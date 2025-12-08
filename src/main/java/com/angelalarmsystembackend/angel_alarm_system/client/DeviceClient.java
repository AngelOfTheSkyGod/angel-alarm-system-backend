package com.angelalarmsystembackend.angel_alarm_system.client;

import com.angelalarmsystembackend.angel_alarm_system.model.AASData;
import com.angelalarmsystembackend.angel_alarm_system.model.SlideShowData;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeviceClient {
    public static void sendPing(String pathName, AASData aasData) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(aasData);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create("http://" + pathName + "/ping"))
                .header("Content-Type", "application/json")
                .build();
        System.out.println("path name: " + pathName);
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());

    }


    public static SlideShowData sendConnect(String pathName, AASData aasData) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(aasData);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create("http://" + pathName + "/connect"))
                .header("Content-Type", "application/json")
                .build();
        System.out.println("path name: " + pathName);
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
        String jsonBody = response.body();

        ObjectMapper mapper = new ObjectMapper();
        SlideShowData obj = mapper.readValue(jsonBody, SlideShowData.class);
        return obj;
    }
}
