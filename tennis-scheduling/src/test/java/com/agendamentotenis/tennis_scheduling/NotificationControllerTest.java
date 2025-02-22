package com.agendamentotenis.tennis_scheduling;

import com.agendamentotenis.controller.NotificationController;
import com.agendamentotenis.model.User;
import com.agendamentotenis.model.Notification;
import com.agendamentotenis.service.NotificationService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationControllerTest {

  @Mock
  private NotificationService notificationService;

  @Mock
  private UserService userService;

  @InjectMocks
  private NotificationController notificationController;

  private User testUser;
  private List<Notification> testNotifications;

  @BeforeEach
  void setUp() {
    testUser = new User("Test User", "test@example.com", "password");
    testNotifications = Arrays.asList(
        new Notification("Test notification 1", testUser),
        new Notification("Test notification 2", testUser));
  }

  @Test
  void getUnreadNotifications_Success() {
    when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    when(notificationService.getUnreadNotifications(testUser)).thenReturn(testNotifications);

    ResponseEntity<List<Notification>> response = notificationController.getUnreadNotifications("test@example.com");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().size());
    verify(notificationService).getUnreadNotifications(testUser);
  }

  @Test
  void getUnreadNotifications_UserNotFound() {
    when(userService.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

    ResponseEntity<List<Notification>> response = notificationController
        .getUnreadNotifications("nonexistent@example.com");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}