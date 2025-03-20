package com.agendamentotenis.controller;

import com.agendamentotenis.model.Match;
import com.agendamentotenis.model.User;
import com.agendamentotenis.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
// @CrossOrigin(origins = "http://localhost:5173")
public class MatchController {

  @Autowired
  private MatchService matchService;

  @PostMapping
  public ResponseEntity<Match> createMatch(@RequestBody Match match) {
    return ResponseEntity.ok(matchService.createMatch(match));
  }

  @GetMapping
  public ResponseEntity<List<Match>> getAllMatches() {
    return ResponseEntity.ok(matchService.getAllMatches());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Match> getMatch(@PathVariable String id) {
    return matchService.getMatchById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/player/{playerId}")
  public ResponseEntity<List<Match>> getMatchesByPlayer(@PathVariable String playerId) {
    User player = new User(null, null, null);
    player.setId(playerId);
    return ResponseEntity.ok(matchService.getMatchesByPlayer(player));
  }

  @GetMapping("/date-range")
  public ResponseEntity<List<Match>> getMatchesByDateRange(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
    return ResponseEntity.ok(matchService.getMatchesByDateRange(start, end));
  }

  @PutMapping("/{id}/status")
  public ResponseEntity<Match> updateMatchStatus(
      @PathVariable String id,
      @RequestBody Match.MatchStatus status) {
    return ResponseEntity.ok(matchService.updateMatchStatus(id, status));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteMatch(@PathVariable String id) {
    matchService.deleteMatch(id);
    return ResponseEntity.ok().build();
  }
}