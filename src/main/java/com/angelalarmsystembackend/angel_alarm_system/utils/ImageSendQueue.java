package com.angelalarmsystembackend.angel_alarm_system.utils;

import com.angelalarmsystembackend.angel_alarm_system.model.AddImageRequest;
import com.angelalarmsystembackend.angel_alarm_system.model.AddImageRequestPi0;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ImageSendQueue {

    private static final BlockingQueue<AddImageRequestPi0> queue = new LinkedBlockingQueue<>();

    public static void enqueue(AddImageRequestPi0 job) {
        queue.offer(job); // non-blocking
    }

    public static AddImageRequestPi0 take() throws InterruptedException {
        return queue.take(); // worker blocks here
    }
}