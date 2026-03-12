package com.angelalarmsystembackend.angel_alarm_system;

import com.angelalarmsystembackend.angel_alarm_system.utils.ImageDeleterWorker;
import com.angelalarmsystembackend.angel_alarm_system.utils.ImageSenderWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AngelAlarmSystemApplication {

	public static void main(String[] args) {
		Thread worker1 = new Thread(new ImageSenderWorker());
		worker1.setDaemon(true);
		worker1.start();
		Thread worker2 = new Thread(new ImageDeleterWorker());
		worker2.setDaemon(true);
		worker2.start();
		SpringApplication.run(AngelAlarmSystemApplication.class, args);
	}

}
