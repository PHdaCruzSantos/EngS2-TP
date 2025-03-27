package com.agendamentotenis.tennis_scheduling;

import com.agendamentotenis.model.Match;
import com.agendamentotenis.model.User;
import com.agendamentotenis.model.Notification;
import com.agendamentotenis.repository.MatchRepository;
import com.agendamentotenis.repository.NotificationRepository;
import com.agendamentotenis.repository.UserRepository;
import com.agendamentotenis.service.MatchService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

  @Mock
  private MatchRepository matchRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private NotificationRepository notificationRepository;

  @InjectMocks
  private MatchService matchService;

  private Match testMatch;
  private User player1;
  private User player2;

  @BeforeEach
  void setUp() {
    player1 = new User("Player 1", "player1@example.com", "password");
    player1.setId("1");
    player2 = new User("Player 2", "player2@example.com", "password");
    player2.setId("2");

    testMatch = new Match(
        LocalDate.now().plusDays(1),
        LocalTime.of(14, 0),
        "Tennis Court 1",
        Arrays.asList(player1, player2));
    testMatch.setId("match1");
  }

  @Test
  void createMatch_Success() {
    // Given
    when(userRepository.existsById(anyString())).thenReturn(true);
    when(matchRepository.save(any(Match.class))).thenReturn(testMatch);
    when(notificationRepository.save(any(Notification.class))).thenReturn(new Notification("test", player1));

    // When
    Match createdMatch = matchService.createMatch(testMatch);

    // Then
    assertNotNull(createdMatch);
    assertEquals(testMatch.getId(), createdMatch.getId());
    assertEquals(Match.MatchStatus.SCHEDULED, createdMatch.getStatus());
    verify(matchRepository).save(any(Match.class));
    verify(notificationRepository, times(2)).save(any(Notification.class));
  }

  @Test
  void createMatch_InvalidPlayer() {
    // Given
    when(userRepository.existsById(anyString())).thenReturn(false);

    // Then
    assertThrows(RuntimeException.class, () -> {
      matchService.createMatch(testMatch);
    });

    verify(matchRepository, never()).save(any(Match.class));
    verify(notificationRepository, never()).save(any(Notification.class));
  }

  @Test
  void createMatch_PastDate() {
    // Given
    testMatch.setDate(LocalDate.now().minusDays(1));

    // Then
    assertThrows(RuntimeException.class, () -> {
      matchService.createMatch(testMatch);
    });

    verify(matchRepository, never()).save(any(Match.class));
    verify(notificationRepository, never()).save(any(Notification.class));
  }

  @Test
  void getAllMatches_Success() {
    // Given
    List<Match> matches = Arrays.asList(testMatch);
    when(matchRepository.findAll()).thenReturn(matches);

    // When
    List<Match> result = matchService.getAllMatches();

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(matchRepository).findAll();
  }

  @Test
  void getMatchById_Success() {
    // Given
    when(matchRepository.findById("match1")).thenReturn(Optional.of(testMatch));

    // When
    Optional<Match> result = matchService.getMatchById("match1");

    // Then
    assertTrue(result.isPresent());
    assertEquals(testMatch.getId(), result.get().getId());
    verify(matchRepository).findById("match1");
  }

  @Test
  void updateMatchStatus_Success() {
    // Given
    when(matchRepository.findById("match1")).thenReturn(Optional.of(testMatch));
    when(matchRepository.save(any(Match.class))).thenReturn(testMatch);

    // When
    Match updatedMatch = matchService.updateMatchStatus("match1", Match.MatchStatus.CANCELLED);

    // Then
    assertNotNull(updatedMatch);
    assertEquals(Match.MatchStatus.CANCELLED, updatedMatch.getStatus());
    verify(matchRepository).save(any(Match.class));
  }

  @Test
  void updateMatchStatus_ToConfirmed_ThrowsException() {
    // Given
    when(matchRepository.findById("match1")).thenReturn(Optional.of(testMatch));

    // Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      matchService.updateMatchStatus("match1", Match.MatchStatus.CONFIRMED);
    });

    assertEquals("Status CONFIRMED deve ser atingido via confirmação de jogadores", exception.getMessage());
    verify(matchRepository, never()).save(any(Match.class));
  }

  @Test
  void updateMatchStatus_NotFound() {
    // Given
    when(matchRepository.findById("nonexistent")).thenReturn(Optional.empty());

    // Then
    assertThrows(RuntimeException.class, () -> {
      matchService.updateMatchStatus("nonexistent", Match.MatchStatus.CONFIRMED);
    });

    verify(matchRepository, never()).save(any(Match.class));
  }

  @Test
  void deleteMatch_Success() {
    // Given
    when(matchRepository.findById("match1")).thenReturn(Optional.of(testMatch));
    doNothing().when(matchRepository).deleteById("match1");

    // When
    matchService.deleteMatch("match1");

    // Then
    verify(matchRepository).deleteById("match1");
    verify(notificationRepository, times(2)).save(any(Notification.class));
  }

  @Test
  void deleteMatch_NotFound() {
    // Given
    when(matchRepository.findById("nonexistent")).thenReturn(Optional.empty());

    // Then
    assertThrows(RuntimeException.class, () -> {
      matchService.deleteMatch("nonexistent");
    });

    verify(matchRepository, never()).deleteById(anyString());
    verify(notificationRepository, never()).save(any(Notification.class));
  }
}