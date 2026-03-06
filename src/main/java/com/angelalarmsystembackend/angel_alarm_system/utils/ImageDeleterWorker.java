package com.angelalarmsystembackend.angel_alarm_system.utils;

import com.angelalarmsystembackend.angel_alarm_system.model.AddImageRequest;
import com.angelalarmsystembackend.angel_alarm_system.model.DeleteImageRequest;
import com.angelalarmsystembackend.angel_alarm_system.model.DeleteImageRequestPi0;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ImageDeleterWorker implements Runnable {

    @Override
    public void run() {

        while (true) {
            try {

                DeleteImageRequestPi0 job = ImageDeleteQueue.take();

                sendToPiZero(job);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void sendToPiZero(DeleteImageRequestPi0 job) throws Exception {

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