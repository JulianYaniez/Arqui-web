package com.arquiweb.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TicketYaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketYaApplication.class, args);
	}

}
