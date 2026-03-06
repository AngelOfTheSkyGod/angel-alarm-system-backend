package com.angelalarmsystembackend.angel_alarm_system.utils;

import com.angelalarmsystembackend.angel_alarm_system.model.AddImageRequest;
import com.angelalarmsystembackend.angel_alarm_system.model.AddImageRequestPi0;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ImageSenderWorker implements Runnable {

    @Override
    public void run() {

        while (true) {
            try {

                AddImageRequestPi0 job = ImageSendQueue.take();

                sendToPiZero(job);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void sendToPiZero(AddImageRequestPi0 job) throws Exception {

        String json = new ObjectMapper().writeValueAsString(job);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(job.getPathName()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.discarding());
    }
}