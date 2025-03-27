package com.agendamentotenis.tennis_scheduling;

import com.agendamentotenis.controller.UserController;
import com.agendamentotenis.model.User;
import com.agendamentotenis.security.JwtConfig;
import com.agendamentotenis.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

  @Mock
  private UserService userService;

  @Mock
  private JwtConfig jwtConfig;

  @InjectMocks
  private UserController userController;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User("Test User", "test@example.com", "password123");
    // Removemos o stubbing do jwtConfig.generateToken daqui
  }

  @Test
  void registerUser_Success() {
    // Given
    when(userService.createUser(any(User.class))).thenReturn(testUser);
    when(jwtConfig.generateToken(anyString())).thenReturn("dummy-token"); // Stubbing movido para cá

    // When
    ResponseEntity<?> response = userController.registerUser(testUser);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Verifica se o corpo da resposta é um Map
    assertTrue(response.getBody() instanceof Map);

    // Converte o corpo da resposta para um Map
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

    // Verifica se o Map contém o token e o usuário
    assertEquals("dummy-token", responseBody.get("token")); // Verifica o token
    assertEquals(testUser, responseBody.get("user")); // Verifica o usuário

    verify(userService).createUser(testUser);
  }

  @Test
  void registerDuplicateEmail_Failure() {
    // Given
    when(userService.createUser(any(User.class))).thenThrow(new RuntimeException("Email já está em uso"));

    // When
    assertThrows(RuntimeException.class, () -> userController.registerUser(testUser));

    // Then
    verify(userService).createUser(testUser);
  }

  @Test
  void getAllUsers_Success() {
    // Given
    List<User> users = Arrays.asList(
        new User("User1", "user1@example.com", "pass1"),
        new User("User2", "user2@example.com", "pass2"));
    when(userService.findAllUsers()).thenReturn(users);

    // When
    ResponseEntity<List<User>> response = userController.getAllUsers();

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().size());
    verify(userService).findAllUsers();
  }

  @Test
  void getUser_Success() {
    // Given
    String email = "test@example.com";
    when(userService.findByEmail(email)).thenReturn(Optional.of(testUser));

    // When
    ResponseEntity<User> response = userController.getUser(email);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(testUser, response.getBody());
    verify(userService).findByEmail(email);
  }

  @Test
  void getUser_NotFound() {
    // Given
    String email = "nonexistent@example.com";
    when(userService.findByEmail(email)).thenReturn(Optional.empty());

    // When
    ResponseEntity<User> response = userController.getUser(email);

    // Then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(userService).findByEmail(email);
  }

  @Test
  void updateUser_Success() {
    // Given
    String id = "test@example.com";
    User updatedUser = new User("Updated User", "test@example.com", "newpassword");
    when(userService.updateUser(id, testUser)).thenReturn(updatedUser);

    // When
    ResponseEntity<User> response = userController.updateUser(id, testUser);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(updatedUser, response.getBody());
    verify(userService).updateUser(id, testUser);
  }

  @Test
  void deleteUser_Success() {
    // Given
    String id = "test@example.com";
    doNothing().when(userService).deleteUser(id);

    // When
    ResponseEntity<?> response = userController.deleteUser(id);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(userService).deleteUser(id);
  }
}