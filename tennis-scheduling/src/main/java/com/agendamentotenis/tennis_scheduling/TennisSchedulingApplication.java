package com.agendamentotenis.tennis_scheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.agendamentotenis" })
public class TennisSchedulingApplication {

	public static void main(String[] args) {
		SpringApplication.run(TennisSchedulingApplication.class, args);
	}
}