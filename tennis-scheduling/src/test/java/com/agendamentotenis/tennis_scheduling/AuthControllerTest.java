package com.agendamentotenis.tennis_scheduling;

import com.agendamentotenis.controller.AuthController;
import com.agendamentotenis.model.User;
import com.agendamentotenis.service.UserService;
import com.agendamentotenis.security.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

  @Mock
  private UserService userService;

  @Mock
  private JwtConfig jwtConfig;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private AuthController authController;

  private User testUser;
  private static final String TEST_TOKEN = "test.jwt.token";

  @BeforeEach
  void setUp() {
    testUser = new User("Test User", "test@example.com", "password123");
  }

  @Test
  void login_Success() {
    // Given
    User loginUser = new User(null, "test@example.com", "password123");
    when(userService.findByEmail(anyString())).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    when(jwtConfig.generateToken(anyString())).thenReturn(TEST_TOKEN);

    // When
    ResponseEntity<?> response = authController.login(loginUser);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody() instanceof Map);

    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertEquals(TEST_TOKEN, responseBody.get("token"));
    assertEquals(testUser, responseBody.get("user"));
  }

  @Test
  void login_InvalidCredentials() {
    // Given
    User loginUser = new User(null, "test@example.com", "wrongpassword");
    when(userService.findByEmail(anyString())).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

    // When
    ResponseEntity<?> response = authController.login(loginUser);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody() instanceof Map);

    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertEquals("Credenciais inválidas", responseBody.get("error"));
  }

  @Test
  void login_UserNotFound() {
    // Given
    User loginUser = new User(null, "nonexistent@example.com", "password123");
    when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

    // When
    ResponseEntity<?> response = authController.login(loginUser);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody() instanceof Map);

    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertEquals("Credenciais inválidas", responseBody.get("error"));
  }

  @Test
  void login_WithNullEmail() {
    // Given
    User loginUser = new User(null, null, "password123");

    // When
    ResponseEntity<?> response = authController.login(loginUser);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void login_WithNullPassword() {
    // Given
    User loginUser = new User(null, "test@example.com", null);

    // When
    ResponseEntity<?> response = authController.login(loginUser);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
}
