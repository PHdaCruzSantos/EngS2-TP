package com.agendamentotenis.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoRepositories(basePackages = "com.agendamentotenis.repository")
public class MongoDBConfig {

  private final Dotenv dotenv = Dotenv.configure().load();

  @Bean
  public MongoClient mongoClient() {
    String username = dotenv.get("MONGODB_USERNAME");
    String password = dotenv.get("MONGODB_PASSWORD");
    String cluster = dotenv.get("MONGODB_CLUSTER");
    String database = dotenv.get("MONGODB_DATABASE");

    String uri = String.format("mongodb+srv://%s:%s@%s/%s?retryWrites=true&w=majority",
        username, password, cluster, database);

    return MongoClients.create(uri);
  }

  @Bean
  public MongoDatabaseFactory mongoDatabaseFactory() {
    return new SimpleMongoClientDatabaseFactory(mongoClient(), dotenv.get("MONGODB_DATABASE"));
  }

  @Bean
  public MongoTemplate mongoTemplate() {
    return new MongoTemplate(mongoDatabaseFactory());
  }
}