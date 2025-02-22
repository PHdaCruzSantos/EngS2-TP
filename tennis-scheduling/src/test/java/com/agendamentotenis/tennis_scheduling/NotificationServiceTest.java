package com.agendamentotenis.tennis_scheduling;

import com.agendamentotenis.model.User;
import com.agendamentotenis.model.Match;
import com.agendamentotenis.model.Notification;
import com.agendamentotenis.repository.NotificationRepository;
import com.agendamentotenis.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;

  @InjectMocks
  private NotificationService notificationService;

  private User testUser;
  private Match testMatch;

  @BeforeEach
  void setUp() {
    testUser = new User("Test User", "test@example.com", "password");
    testMatch = new Match(
        LocalDate.now().plusDays(1),
        LocalTime.of(14, 0),
        "Tennis Court 1",
        Arrays.asList(testUser));
  }

  @Test
  void createMatchRequestNotification_Success() {
    when(notificationRepository.save(any(Notification.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    notificationService.createMatchRequestNotification(testMatch, testUser);

    verify(notificationRepository).save(any(Notification.class));
  }

  @Test
  void getUnreadNotifications_Success() {
    List<Notification> notifications = Arrays.asList(
        new Notification("Test notification 1", testUser),
        new Notification("Test notification 2", testUser));

    when(notificationRepository.findByRecipientAndReadFalse(testUser))
        .thenReturn(notifications);

    List<Notification> result = notificationService.getUnreadNotifications(testUser);

    assertEquals(2, result.size());
    verify(notificationRepository).findByRecipientAndReadFalse(testUser);
  }
}