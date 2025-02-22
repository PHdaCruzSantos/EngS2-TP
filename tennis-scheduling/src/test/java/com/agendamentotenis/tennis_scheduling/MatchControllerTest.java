package com.agendamentotenis.tennis_scheduling;

import com.agendamentotenis.controller.MatchController;
import com.agendamentotenis.model.Match;
import com.agendamentotenis.model.User;
import com.agendamentotenis.service.MatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchControllerTest {

  @Mock
  private MatchService matchService;

  @InjectMocks
  private MatchController matchController;
  private ObjectMapper objectMapper;

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

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

  }

  @Test
  void createMatch_Success() {
    // Given
    when(matchService.createMatch(any(Match.class))).thenReturn(testMatch);

    // When
    ResponseEntity<Match> response = matchController.createMatch(testMatch);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(testMatch.getId(), response.getBody().getId());

    try {
      System.out.println("\n=== Create Match Response ===");
      System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
          .writeValueAsString(response.getBody()));
      System.out.println("==========================\n");
    } catch (Exception e) {
      e.printStackTrace();
    }

    verify(matchService).createMatch(testMatch);
  }

  @Test
  void getAllMatches_Success() {
    // Given
    List<Match> matches = Arrays.asList(testMatch);
    when(matchService.getAllMatches()).thenReturn(matches);

    // When
    ResponseEntity<List<Match>> response = matchController.getAllMatches();

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    try {
      System.out.println("\n=== Get All Matches Response ===");
      System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
          .writeValueAsString(response.getBody()));
      System.out.println("==============================\n");
    } catch (Exception e) {
      e.printStackTrace();
    }
    verify(matchService).getAllMatches();
  }

  @Test
  void getMatch_Success() {
    // Given
    when(matchService.getMatchById("match1")).thenReturn(Optional.of(testMatch));

    // When
    ResponseEntity<Match> response = matchController.getMatch("match1");

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(testMatch.getId(), response.getBody().getId());
    try {
      System.out.println("\n=== Get Match Response ===");
      System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
          .writeValueAsString(response.getBody()));
      System.out.println("========================\n");
    } catch (Exception e) {
      e.printStackTrace();
    }
    verify(matchService).getMatchById("match1");
  }

  @Test
  void getMatch_NotFound() {
    // Given
    when(matchService.getMatchById("nonexistent")).thenReturn(Optional.empty());

    // When
    ResponseEntity<Match> response = matchController.getMatch("nonexistent");

    // Then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(matchService).getMatchById("nonexistent");
  }

  @Test
  void getMatchesByPlayer_Success() {
    // Given
    List<Match> matches = Arrays.asList(testMatch);
    when(matchService.getMatchesByPlayer(any(User.class))).thenReturn(matches);

    // When
    ResponseEntity<List<Match>> response = matchController.getMatchesByPlayer("1");

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    try {
      System.out.println("\n=== Get Matches By Player Response ===");
      System.out.println("Player ID: 1");
      System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
          .writeValueAsString(response.getBody()));
      System.out.println("=================================\n");
    } catch (Exception e) {
      e.printStackTrace();
    }
    verify(matchService).getMatchesByPlayer(any(User.class));
  }

  @Test
  void getMatchesByDateRange_Success() {
    // Given
    LocalDate start = LocalDate.now();
    LocalDate end = LocalDate.now().plusDays(7);
    List<Match> matches = Arrays.asList(testMatch);
    when(matchService.getMatchesByDateRange(start, end)).thenReturn(matches);

    // When
    ResponseEntity<List<Match>> response = matchController.getMatchesByDateRange(start, end);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    try {
      System.out.println("\n=== Get Matches By Date Range Response ===");
      System.out.println("Date Range: " + start + " to " + end);
      System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
          .writeValueAsString(response.getBody()));
      System.out.println("====================================\n");
    } catch (Exception e) {
      e.printStackTrace();
    }
    verify(matchService).getMatchesByDateRange(start, end);
  }

  @Test
  void updateMatchStatus_Success() {
    // Given
    testMatch.setStatus(Match.MatchStatus.CONFIRMED);
    when(matchService.updateMatchStatus("match1", Match.MatchStatus.CONFIRMED))
        .thenReturn(testMatch);

    // When
    ResponseEntity<Match> response = matchController.updateMatchStatus(
        "match1", Match.MatchStatus.CONFIRMED);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(Match.MatchStatus.CONFIRMED, response.getBody().getStatus());
    verify(matchService).updateMatchStatus("match1", Match.MatchStatus.CONFIRMED);
  }

  @Test
  void deleteMatch_Success() {
    // Given
    doNothing().when(matchService).deleteMatch("match1");

    // When
    ResponseEntity<?> response = matchController.deleteMatch("match1");

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(matchService).deleteMatch("match1");
  }
}