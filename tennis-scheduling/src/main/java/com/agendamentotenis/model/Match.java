package com.agendamentotenis.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
    
    public enum MatchStatus {
        SCHEDULED,
        CONFIRMED,
        CANCELLED,
        COMPLETED
    }
    
    // Construtor
    public Match(LocalDate date, LocalTime time, String location, List<User> players) {
        this.date = date;
        this.time = time;
        this.location = location;
        this.players = players;
        this.status = MatchStatus.SCHEDULED;
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public List<User> getPlayers() { return players; }
    public void setPlayers(List<User> players) { this.players = players; }
    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }
}