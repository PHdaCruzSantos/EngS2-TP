package com.agendamentotenis.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    private String message;
    
    @DBRef
    private User recipient;
    private boolean read;
    private LocalDate createdAt;
    
    // Construtor
    public Notification(String message, User recipient) {
        this.message = message;
        this.recipient = recipient;
        this.read = false;
        this.createdAt = LocalDate.now();
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}