package com.agendamentotenis.service;

import com.agendamentotenis.model.Match;
import com.agendamentotenis.model.User;
import com.agendamentotenis.model.Notification;
import com.agendamentotenis.repository.MatchRepository;
import com.agendamentotenis.repository.NotificationRepository;
import com.agendamentotenis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

  @Autowired
  private MatchRepository matchRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private NotificationRepository notificationRepository;

  public Match createMatch(Match match) {
    // Validar se todos os jogadores existem
    for (User player : match.getPlayers()) {
      if (!userRepository.existsById(player.getId())) {
        throw new RuntimeException("Jogador não encontrado: " + player.getId());
      }
    }

    // Validar data e hora
    if (match.getDate().isBefore(LocalDate.now())) {
      throw new RuntimeException("Data da partida não pode ser no passado");
    }

    // Definir status inicial
    match.setStatus(Match.MatchStatus.SCHEDULED);

    // Salvar a partida
    Match savedMatch = matchRepository.save(match);

    // Enviar notificações para os jogadores
    for (User player : match.getPlayers()) {
      Notification notification = new Notification(
          "Você foi convidado para uma partida em " + match.getDate() + " às " + match.getTime(),
          player);
      notificationRepository.save(notification);
    }

    return savedMatch;
  }

  public List<Match> getAllMatches() {
    return matchRepository.findAll();
  }

  public Optional<Match> getMatchById(String id) {
    return matchRepository.findById(id);
  }

  public List<Match> getMatchesByPlayer(User player) {
    return matchRepository.findByPlayersContaining(player);
  }

  public List<Match> getMatchesByDateRange(LocalDate start, LocalDate end) {
    return matchRepository.findByDateBetween(start, end);
  }

  public Match updateMatchStatus(String matchId, Match.MatchStatus newStatus) {
    Match match = matchRepository.findById(matchId)
        .orElseThrow(() -> new RuntimeException("Partida não encontrada"));

    match.setStatus(newStatus);
    return matchRepository.save(match);
  }

  public void deleteMatch(String id) {
    Match match = matchRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Partida não encontrada"));

    // Notificar jogadores sobre o cancelamento
    for (User player : match.getPlayers()) {
      Notification notification = new Notification(
          "A partida agendada para " + match.getDate() + " foi cancelada",
          player);
      notificationRepository.save(notification);
    }

    matchRepository.deleteById(id);
  }
}