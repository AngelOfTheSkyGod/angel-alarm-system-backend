package com.angelalarmsystembackend.angel_alarm_system.utils;

import com.angelalarmsystembackend.angel_alarm_system.model.AddImageRequest;
import com.angelalarmsystembackend.angel_alarm_system.model.DeleteImageRequest;
import com.angelalarmsystembackend.angel_alarm_system.model.DeleteImageRequestPi0;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ImageDeleteQueue {

    private static final BlockingQueue<DeleteImageRequestPi0> queue = new LinkedBlockingQueue<>();

    public static void enqueue(DeleteImageRequestPi0 job) {
        queue.offer(job); // non-blocking
    }

    public static DeleteImageRequestPi0 take() throws InterruptedException {
        return queue.take(); // worker blocks here
    }
}