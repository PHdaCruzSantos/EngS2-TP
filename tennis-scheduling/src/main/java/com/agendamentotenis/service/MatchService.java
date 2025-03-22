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
import java.util.stream.Collectors;

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

  // Método atualizado para gerenciar status apenas para casos específicos (não
  // confirmação)
  public Match updateMatchStatus(String matchId, Match.MatchStatus newStatus) {
    Match match = matchRepository.findById(matchId)
        .orElseThrow(() -> new RuntimeException("Partida não encontrada"));

    // Não permite mudar para CONFIRMED diretamente - deve ser via confirmação de
    // jogadores
    if (newStatus == Match.MatchStatus.CONFIRMED) {
      throw new RuntimeException("Status CONFIRMED deve ser atingido via confirmação de jogadores");
    }

    match.setStatus(newStatus);

    // Se a partida for cancelada, limpa as confirmações
    if (newStatus == Match.MatchStatus.CANCELLED) {
      match.setConfirmedPlayerIds(null);
    }

    return matchRepository.save(match);
  }

  // Novo método para confirmar participação de um jogador
  public Match confirmPlayerParticipation(String matchId, String playerId) {
    Match match = matchRepository.findById(matchId)
        .orElseThrow(() -> new RuntimeException("Partida não encontrada"));

    // Verifica se o jogador está realmente nesta partida
    boolean isPlayerInMatch = match.getPlayers().stream()
        .anyMatch(player -> player.getId().equals(playerId));

    if (!isPlayerInMatch) {
      throw new RuntimeException("Jogador não está registrado nesta partida");
    }

    // Adiciona confirmação do jogador
    match.addConfirmedPlayer(playerId);

    // Verifica se todos confirmaram e atualiza o status automaticamente
    if (match.areAllPlayersConfirmed()) {
      match.setStatus(Match.MatchStatus.CONFIRMED);

      // Notifica todos os jogadores que a partida foi confirmada
      for (User player : match.getPlayers()) {
        Notification notification = new Notification(
            "A partida agendada para " + match.getDate() + " às " + match.getTime()
                + " foi confirmada por todos os jogadores",
            player);
        notificationRepository.save(notification);
      }
    } else {
      // Notifica os outros jogadores que um jogador confirmou
      User confirmedPlayer = match.getPlayers().stream()
          .filter(player -> player.getId().equals(playerId))
          .findFirst()
          .orElse(null);

      String playerName = confirmedPlayer != null ? confirmedPlayer.getName() : "Um jogador";

      for (User player : match.getPlayers()) {
        if (!player.getId().equals(playerId)) {
          Notification notification = new Notification(
              playerName + " confirmou participação na partida de " + match.getDate() + ". Aguardando sua confirmação.",
              player);
          notificationRepository.save(notification);
        }
      }
    }

    return matchRepository.save(match);
  }

  // Método para cancelar a confirmação de um jogador
  public Match unconfirmPlayerParticipation(String matchId, String playerId) {
    Match match = matchRepository.findById(matchId)
        .orElseThrow(() -> new RuntimeException("Partida não encontrada"));

    // Remove a confirmação do jogador
    match.removeConfirmedPlayer(playerId);

    // Se a partida estava confirmada, volta para agendada
    if (match.getStatus() == Match.MatchStatus.CONFIRMED) {
      match.setStatus(Match.MatchStatus.SCHEDULED);

      // Notifica os outros jogadores que alguém cancelou a confirmação
      User unconfirmedPlayer = match.getPlayers().stream()
          .filter(player -> player.getId().equals(playerId))
          .findFirst()
          .orElse(null);

      String playerName = unconfirmedPlayer != null ? unconfirmedPlayer.getName() : "Um jogador";

      for (User player : match.getPlayers()) {
        if (!player.getId().equals(playerId)) {
          Notification notification = new Notification(
              playerName + " cancelou a confirmação da partida de " + match.getDate()
                  + ". A partida voltou ao status 'Agendada'.",
              player);
          notificationRepository.save(notification);
        }
      }
    }

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