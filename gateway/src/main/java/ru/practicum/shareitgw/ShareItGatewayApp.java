package ru.practicum.shareitgw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ShareItGatewayApp {

	public static void main(String[] args) {
		SpringApplication.run(ShareItGatewayApp.class, args);
	}

}
