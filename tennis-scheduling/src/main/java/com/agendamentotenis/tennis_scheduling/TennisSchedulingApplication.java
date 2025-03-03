package com.agendamentotenis.tennis_scheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.agendamentotenis.config.EnvPropertySource;

@SpringBootApplication
@ComponentScan(basePackages = { "com.agendamentotenis" })
public class TennisSchedulingApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(TennisSchedulingApplication.class);
		application.addInitializers(new EnvPropertySource());
		application.run(args);
	}
}