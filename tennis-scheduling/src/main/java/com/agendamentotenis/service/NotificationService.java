package com.agendamentotenis.service;

import com.agendamentotenis.model.User;
import com.agendamentotenis.model.Notification;
import com.agendamentotenis.model.Match;
import com.agendamentotenis.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

  @Autowired
  private NotificationRepository notificationRepository;

  public void createMatchRequestNotification(Match match, User recipient) {
    String message = String.format("Você foi convidado para uma partida em %s às %s na %s",
        match.getDate(), match.getTime(), match.getLocation());
    createNotification(message, recipient);
  }

  public void createMatchConfirmationNotification(Match match, User recipient) {
    String message = String.format("Sua partida em %s às %s na %s foi confirmada",
        match.getDate(), match.getTime(), match.getLocation());
    createNotification(message, recipient);
  }

  public void createMatchCancellationNotification(Match match, User recipient) {
    String message = String.format("A partida em %s às %s na %s foi cancelada",
        match.getDate(), match.getTime(), match.getLocation());
    createNotification(message, recipient);
  }

  public void createMatchUpdateNotification(Match match, User recipient) {
    String message = String.format("Houve alterações na partida marcada para %s às %s na %s",
        match.getDate(), match.getTime(), match.getLocation());
    createNotification(message, recipient);
  }

  private Notification createNotification(String message, User recipient) {
    Notification notification = new Notification(message, recipient);
    return notificationRepository.save(notification);
  }

  public List<Notification> getUnreadNotifications(User user) {
    return notificationRepository.findByRecipientAndReadFalse(user);
  }

  public List<Notification> getAllNotifications(User user) {
    return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
  }

  public void markAsRead(String notificationId) {
    notificationRepository.findById(notificationId).ifPresent(notification -> {
      notification.setRead(true);
      notificationRepository.save(notification);
    });
  }

  public void markAllAsRead(User user) {
    List<Notification> unreadNotifications = notificationRepository.findByRecipientAndReadFalse(user);
    unreadNotifications.forEach(notification -> {
      notification.setRead(true);
      notificationRepository.save(notification);
    });
  }
}