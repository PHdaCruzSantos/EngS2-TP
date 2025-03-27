package com.agendamentotenis.tennis_scheduling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MongoIntegrationTest {

  @Autowired
  private MongoTemplate mongoTemplate;

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
  void testMongoConnection() {
    // Test connection and verify collections exist
    assertTrue(mongoTemplate.collectionExists("users"));
    assertTrue(mongoTemplate.collectionExists("matches"));
    assertTrue(mongoTemplate.collectionExists("notifications"));

    // Additional verification
    int collectionCount = mongoTemplate.getCollectionNames().size();
    assertTrue(collectionCount >= 3, "Expected at least 3 collections but found " + collectionCount);
  }
}