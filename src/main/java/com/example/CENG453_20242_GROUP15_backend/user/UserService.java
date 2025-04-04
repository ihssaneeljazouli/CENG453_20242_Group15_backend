package com.example.CENG453_20242_GROUP15_backend.user;

import com.example.CENG453_20242_GROUP15_backend.leaderboard.LeaderboardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    // Register a new user
    public UserEntity registerUser(UserEntity user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already in use.");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already in use.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Find user by email
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Verify user credentials for login
    public boolean authenticateUser(String email, String rawPassword) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        return user.isPresent() && passwordEncoder.matches(rawPassword, user.get().getPassword());
    }

    // Generate and set a password reset token
    public String generateResetToken(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found.");
        }
        String token = UUID.randomUUID().toString();
        user.get().setResetToken(token);
        userRepository.save(user.get());
        return token;
    }

    // Reset user password
    public void resetPassword(String token, String newPassword) {
        Optional<UserEntity> user = userRepository.findByResetToken(token);
        if (user.isEmpty()) {
            throw new RuntimeException("Invalid or expired reset token.");
        }
        user.get().setPassword(passwordEncoder.encode(newPassword)); // Encrypt new password
        user.get().setResetToken(null); // Clear the reset token
        userRepository.save(user.get());
    }

    // Get all-time leaderboard
    public List<LeaderboardDTO> getAllTimeLeaderboard() {
        List<UserEntity> users = userRepository.findTopUsersAllTime();
        return users.stream()
                .map(user -> new LeaderboardDTO(user.getUsername(), user.getScore()))
                .collect(Collectors.toList());
    }

    // Get weekly leaderboard
    public List<LeaderboardDTO> getWeeklyLeaderboard() {
        LocalDateTime weekStart = LocalDateTime.now().minusWeeks(1);
        List<UserEntity> users = userRepository.findTopUsersThisWeek(weekStart);
        return users.stream()
                .map(user -> new LeaderboardDTO(user.getUsername(), user.getScore()))
                .collect(Collectors.toList());
    }

    // Get monthly leaderboard
    public List<LeaderboardDTO> getMonthlyLeaderboard() {
        LocalDateTime monthStart = LocalDateTime.now().minusMonths(1);
        List<UserEntity> users = userRepository.findTopUsersThisMonth(monthStart);
        return users.stream()
                .map(user -> new LeaderboardDTO(user.getUsername(), user.getScore()))
                .collect(Collectors.toList());
    }

    // Method to update user's score after each game
    public void updateUserScore(Long userId, Integer newScore) {
        UserEntity user = userRepository.findById(Math.toIntExact(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        user.setScore(newScore);
        user.setLastPlayed(LocalDateTime.now());
        userRepository.save(user);
    }
}

