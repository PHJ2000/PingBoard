package com.pingboard;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableScheduling
@SpringBootApplication
public class PingBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(PingBoardApplication.class, args);
	}

}
