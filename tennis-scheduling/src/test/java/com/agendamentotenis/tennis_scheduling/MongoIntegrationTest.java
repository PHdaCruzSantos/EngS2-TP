package com.agendamentotenis.tennis_scheduling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ActiveProfiles;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import io.github.cdimascio.dotenv.Dotenv;

import static org.junit.jupiter.api.Assertions.*;

import org.bson.Document;

@SpringBootTest
@EnableMongoRepositories(basePackages = "com.agendamentotenis.repository")
@ActiveProfiles("test")
public class MongoIntegrationTest {

  @Autowired
  private MongoTemplate mongoTemplate;

  private static final Dotenv dotenv = Dotenv.configure().load();

  @BeforeEach
  void setUp() {
    // Create test collections if they don't exist
    if (!mongoTemplate.collectionExists("users")) {
      mongoTemplate.createCollection("users");
    }
    if (!mongoTemplate.collectionExists("matches")) {
      mongoTemplate.createCollection("matches");
    }
    if (!mongoTemplate.collectionExists("notifications")) {
      mongoTemplate.createCollection("notifications");
    }
  }

  @Test
  public void testConnection() {
    // Test that mongoTemplate is properly injected and working
    assertNotNull(mongoTemplate);
    assertNotNull(mongoTemplate.getDb());

    // Run a ping command to verify the connection
    Document pingResult = mongoTemplate.getDb().runCommand(new Document("ping", 1));
    assertEquals(1, pingResult.get("ok"));

    // Print database name for verification

    // Verify that we can perform basic operations
    String collectionName = "test_connection_" + System.currentTimeMillis();
    mongoTemplate.createCollection(collectionName);
    assertTrue(mongoTemplate.collectionExists(collectionName));

    // Clean up
    mongoTemplate.dropCollection(collectionName);
  }
}