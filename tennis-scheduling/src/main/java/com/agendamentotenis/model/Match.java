package com.agendamentotenis.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "matches")
public class Match {
    @Id
    private String id;
    private LocalDate date;
    private LocalTime time;
    private String location;

    @DBRef
    private List<User> players;
    private MatchStatus status;

    // Conjunto para armazenar os IDs dos jogadores que confirmaram
    private Set<String> confirmedPlayerIds;

    public enum MatchStatus {
        SCHEDULED,
        CONFIRMED,
        CANCELLED,
        COMPLETED
    }

    // Add a default constructor
    public Match() {
        this.players = new ArrayList<>();
        this.status = MatchStatus.SCHEDULED;
        this.confirmedPlayerIds = new HashSet<>();
    }

    // Construtor
    public Match(LocalDate date, LocalTime time, String location, List<User> players) {
        this.date = date;
        this.time = time;
        this.location = location;
        this.players = players != null ? players : new ArrayList<>();
        this.status = MatchStatus.SCHEDULED;
        this.confirmedPlayerIds = new HashSet<>();
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<User> getPlayers() {
        return players;
    }

    public void setPlayers(List<User> players) {
        this.players = players;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public Set<String> getConfirmedPlayerIds() {
        return confirmedPlayerIds;
    }

    public void setConfirmedPlayerIds(Set<String> confirmedPlayerIds) {
        this.confirmedPlayerIds = confirmedPlayerIds;
    }

    // Método para adicionar um jogador à lista de confirmados
    public void addConfirmedPlayer(String playerId) {
        if (this.confirmedPlayerIds == null) {
            this.confirmedPlayerIds = new HashSet<>();
        }
        this.confirmedPlayerIds.add(playerId);

        // Se todos os jogadores confirmaram, atualize o status automaticamente
        if (this.players != null &&
                this.confirmedPlayerIds.size() == this.players.size() &&
                this.status == MatchStatus.SCHEDULED) {
            this.status = MatchStatus.CONFIRMED;
        }
    }

    // Método para remover um jogador da lista de confirmados
    public void removeConfirmedPlayer(String playerId) {
        if (this.confirmedPlayerIds != null) {
            this.confirmedPlayerIds.remove(playerId);
        }

        // Se algum jogador retirou a confirmação, volte ao status agendado
        if (this.status == MatchStatus.CONFIRMED) {
            this.status = MatchStatus.SCHEDULED;
        }
    }

    // Verifica se um jogador já confirmou a partida
    public boolean isPlayerConfirmed(String playerId) {
        return this.confirmedPlayerIds != null && this.confirmedPlayerIds.contains(playerId);
    }

    // Verifica se todos os jogadores confirmaram a partida
    public boolean areAllPlayersConfirmed() {
        return this.players != null &&
                this.confirmedPlayerIds != null &&
                this.confirmedPlayerIds.size() == this.players.size();
    }
}