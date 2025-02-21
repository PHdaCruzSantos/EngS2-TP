package com.agendamentotenis.repository;
import com.agendamentotenis.model.User;
import com.agendamentotenis.model.Notification;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, String> {
  List<Notification> findByRecipientAndReadFalse(User recipient);
  List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
}