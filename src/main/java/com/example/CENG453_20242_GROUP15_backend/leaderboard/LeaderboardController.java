package com.example.CENG453_20242_GROUP15_backend.leaderboard;


import com.example.CENG453_20242_GROUP15_backend.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    @Autowired
    private UserService userService;

    // Get all-time leaderboard
    @GetMapping("/all-time")
    public List<LeaderboardDTO> getAllTimeLeaderboard() {
        return userService.getAllTimeLeaderboard();
    }

    // Get weekly leaderboard
    @GetMapping("/weekly")
    public List<LeaderboardDTO> getWeeklyLeaderboard() {
        return userService.getWeeklyLeaderboard();
    }

    // Get monthly leaderboard
    @GetMapping("/monthly")
    public List<LeaderboardDTO> getMonthlyLeaderboard() {
        return userService.getMonthlyLeaderboard();
    }
}

