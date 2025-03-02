package com.agendamentotenis.tennis_scheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@ComponentScan(basePackages = { "com.agendamentotenis" })
public class TennisSchedulingApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().load();

		// Set environment variables for MongoDB connection
		System.setProperty("MONGODB_USERNAME", dotenv.get("MONGODB_USERNAME"));
		System.setProperty("MONGODB_PASSWORD", dotenv.get("MONGODB_PASSWORD"));
		System.setProperty("MONGODB_CLUSTER", dotenv.get("MONGODB_CLUSTER"));
		System.setProperty("MONGODB_DATABASE", dotenv.get("MONGODB_DATABASE"));

		SpringApplication.run(TennisSchedulingApplication.class, args);
	}
}