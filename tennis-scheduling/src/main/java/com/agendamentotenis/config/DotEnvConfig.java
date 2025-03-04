package com.agendamentotenis.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DotEnvConfig {

  @PostConstruct
  public void init() {
    Dotenv dotenv = Dotenv.configure().load();

    // Set system properties from .env file
    dotenv.entries().forEach(entry -> {
      System.setProperty(entry.getKey(), entry.getValue());
    });
  }
}