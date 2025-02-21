package com.agendamentotenis.repository;
import com.agendamentotenis.model.Match;
import com.agendamentotenis.model.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends MongoRepository<Match, String> {
    List<Match> findByPlayersContaining(User player);
    List<Match> findByDateBetween(LocalDate start, LocalDate end);
    List<Match> findByLocation(String location);
}