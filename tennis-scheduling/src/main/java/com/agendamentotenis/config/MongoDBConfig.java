package com.agendamentotenis.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoRepositories(basePackages = "com.agendamentotenis.repository")
public class MongoDBConfig {

  private final Dotenv dotenv = Dotenv.configure().load();

  @Bean
  public MongoClientSettings mongoClientSettings() {
    String username = dotenv.get("MONGODB_USERNAME");
    String password = dotenv.get("MONGODB_PASSWORD");
    String cluster = dotenv.get("MONGODB_CLUSTER");
    String database = dotenv.get("MONGODB_DATABASE");

    MongoCredential credential = MongoCredential.createCredential(
        username,
        "admin", // banco de dados de autenticação
        password.toCharArray());

    ConnectionString connectionString = new ConnectionString(
        String.format("mongodb+srv://%s:%s@%s/%s?retryWrites=true&w=majority",
            username, password, cluster, database));

    return MongoClientSettings.builder()
        .applyConnectionString(connectionString)
        .credential(credential)
        .build();
  }

  @Bean
  @Primary
  public MongoClient mongoClient() {
    return MongoClients.create(mongoClientSettings());
  }

  @Bean
  @Primary
  public MongoDatabaseFactory mongoDatabaseFactory() {
    String username = dotenv.get("MONGODB_USERNAME");
    String password = dotenv.get("MONGODB_PASSWORD");
    String cluster = dotenv.get("MONGODB_CLUSTER");
    String database = dotenv.get("MONGODB_DATABASE");

    String connectionString = String.format("mongodb+srv://%s:%s@%s/%s?retryWrites=true&w=majority",
        username, password, cluster, database);

    return new SimpleMongoClientDatabaseFactory(connectionString);
  }

  @Bean
  @Primary
  public MongoTemplate mongoTemplate() {
    return new MongoTemplate(mongoDatabaseFactory());
  }
}