package com.agendamentotenis.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoRepositories(basePackages = "com.agendamentotenis")
public class MongoConfig extends AbstractMongoClientConfiguration {

  private final Dotenv dotenv = Dotenv.configure().load();

  @Override
  protected String getDatabaseName() {
    return dotenv.get("MONGODB_DATABASE");
  }

  @Override
  public MongoClient mongoClient() {
    String username = dotenv.get("MONGODB_USERNAME");
    String password = dotenv.get("MONGODB_PASSWORD");
    String cluster = dotenv.get("MONGODB_CLUSTER");
    String uri = String.format("mongodb+srv://%s:%s@%s/?retryWrites=true&w=majority",
        username, password, cluster);

    return MongoClients.create(uri);
  }

  @Bean
  MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
    return new MongoTransactionManager(dbFactory);
  }
}