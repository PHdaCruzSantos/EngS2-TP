package com.agendamentotenis.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class EnvPropertySource implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    ConfigurableEnvironment environment = applicationContext.getEnvironment();
    Dotenv dotenv = Dotenv.configure().load();

    Map<String, Object> envProperties = new HashMap<>();
    dotenv.entries().forEach(entry -> {
      envProperties.put(entry.getKey(), entry.getValue());
    });

    MapPropertySource propertySource = new MapPropertySource("dotenv", envProperties);
    environment.getPropertySources().addFirst(propertySource);
  }
}