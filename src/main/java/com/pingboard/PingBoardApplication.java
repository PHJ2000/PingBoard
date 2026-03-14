package com.pingboard;

import com.pingboard.alert.config.AlertProperties;
import com.pingboard.security.config.OperatorSecurityProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties({AlertProperties.class, OperatorSecurityProperties.class})
public class PingBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(PingBoardApplication.class, args);
	}

}
