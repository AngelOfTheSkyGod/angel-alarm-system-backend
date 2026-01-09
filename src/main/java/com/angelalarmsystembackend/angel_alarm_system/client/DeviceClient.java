package com.angelalarmsystembackend.angel_alarm_system.client;

import com.angelalarmsystembackend.angel_alarm_system.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

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

    public static SlideShowData sendSlideShowConnect(String pathName) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://" + pathName + "/connectSlideShow"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<InputStream> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofInputStream());
        try (InputStream is = response.body()) {
            // Read in chunks
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int n;
            while ((n = is.read(chunk)) != -1) {
                buffer.write(chunk, 0, n);
            }

            String json = buffer.toString(StandardCharsets.UTF_8);
            return new ObjectMapper().readValue(json, SlideShowData.class);
        }
    }

    public static AASData sendConnect(String pathName) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://" + pathName + "/connect"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<InputStream> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofInputStream());
        try (InputStream is = response.body()) {
            // Read in chunks
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int n;
            while ((n = is.read(chunk)) != -1) {
                buffer.write(chunk, 0, n);
            }

            String json = buffer.toString(StandardCharsets.UTF_8);
            return new ObjectMapper().readValue(json, AASData.class);
        }
    }

    public static ImageRequestResponse addImage(String pathName, String imageDataUrl) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(SlideShowPictureData.builder().imageDataUrl(imageDataUrl).build());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create("http://" + pathName + "/addImage"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
        ImageRequestResponse requestResponse = objectMapper.readValue(
                response.body(),
                ImageRequestResponse.class
        );
        System.out.println("add image success: " + requestResponse.isSuccess());
        return requestResponse;
    }

    public static ImageRequestResponse deleteImage(String pathName, Integer imagePosition) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(DeleteImageRequest.builder().imagePosition(imagePosition).build());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create("http://" + pathName + "/deleteImage"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
        ImageRequestResponse requestResponse = objectMapper.readValue(
                response.body(),
                ImageRequestResponse.class
        );
        System.out.println("delete image success: " + requestResponse.isSuccess());
        return requestResponse;
    }
}
