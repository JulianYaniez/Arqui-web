package com.arquiweb.management;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(
		servers = {
				@Server(url = "/api/management", description = "API Gateway Route")
		}
)
public class TicketYaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketYaApplication.class, args);
	}

}
