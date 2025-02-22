package com.agendamentotenis.controller;

import com.agendamentotenis.model.Notification;
import com.agendamentotenis.model.User;
import com.agendamentotenis.service.NotificationService;
import com.agendamentotenis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private UserService userService;

  @GetMapping("/unread/{userId}")
  public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable String userId) {
    return userService.findByEmail(userId)
        .map(user -> ResponseEntity.ok(notificationService.getUnreadNotifications(user)))
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/all/{userId}")
  public ResponseEntity<List<Notification>> getAllNotifications(@PathVariable String userId) {
    return userService.findByEmail(userId)
        .map(user -> ResponseEntity.ok(notificationService.getAllNotifications(user)))
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{notificationId}/read")
  public ResponseEntity<?> markAsRead(@PathVariable String notificationId) {
    notificationService.markAsRead(notificationId);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/read-all/{userId}")
  public ResponseEntity<?> markAllAsRead(@PathVariable String userId) {
    return userService.findByEmail(userId)
        .map(user -> {
          notificationService.markAllAsRead(user);
          return ResponseEntity.ok().build();
        })
        .orElse(ResponseEntity.notFound().build());
  }
}