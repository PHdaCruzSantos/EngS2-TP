package com.agendamentotenis.tennis_scheduling;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoDatabase;

import org.bson.Document;

@ExtendWith(MockitoExtension.class)
public class MongoIntegrationTest {

  @Mock
  private MongoTemplate mongoTemplate;

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
  }
}