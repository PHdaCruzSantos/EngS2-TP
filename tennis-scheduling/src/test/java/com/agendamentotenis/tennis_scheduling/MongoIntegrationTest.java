package com.agendamentotenis.tennis_scheduling;

<<<<<<< HEAD
import static org.mockito.Mockito.*;
=======
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
>>>>>>> parent of ba5bbda (build: environment variables and configs)
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoDatabase;

<<<<<<< HEAD
import org.bson.Document;

@ExtendWith(MockitoExtension.class)
=======
@SpringBootTest
>>>>>>> parent of ba5bbda (build: environment variables and configs)
public class MongoIntegrationTest {

  @Mock
  private MongoTemplate mongoTemplate;

<<<<<<< HEAD
  @Mock
  private MongoDatabase mongoDatabase;

  @Test
  public void testConnectionWithMock() {
    // Configurar mocks
    Document pingResponse = new Document("ok", 1.0);
    when(mongoTemplate.getDb()).thenReturn(mongoDatabase);
    when(mongoDatabase.runCommand(any(Document.class))).thenReturn(pingResponse);
    when(mongoDatabase.getName()).thenReturn("test_db");

    // Testar
    Document result = mongoTemplate.getDb().runCommand(new Document("ping", 1));
    assertEquals(1.0, result.getDouble("ok"));
    System.out.println("Mock de conexão com MongoDB bem-sucedido: " + mongoTemplate.getDb().getName());
=======
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
>>>>>>> parent of ba5bbda (build: environment variables and configs)
  }
}