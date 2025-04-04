package com.example.CENG453_20242_GROUP15_backend.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Integer> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByResetToken(String resetToken);

    // All-time leaderboard: top scores
    @Query("SELECT u FROM UserEntity u ORDER BY u.score DESC")
    List<UserEntity> findTopUsersAllTime();

    // Weekly leaderboard: top scores in the past 7 days
    @Query("SELECT u FROM UserEntity u WHERE u.lastPlayed >= :weekStart ORDER BY u.score DESC")
    List<UserEntity> findTopUsersThisWeek(@Param("weekStart") LocalDateTime weekStart);

    // Monthly leaderboard: top scores in the past 30 days
    @Query("SELECT u FROM UserEntity u WHERE u.lastPlayed >= :monthStart ORDER BY u.score DESC")
    List<UserEntity> findTopUsersThisMonth(@Param("monthStart") LocalDateTime monthStart);

}
