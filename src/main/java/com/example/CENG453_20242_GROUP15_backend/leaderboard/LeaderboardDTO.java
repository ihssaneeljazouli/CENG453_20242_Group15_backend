package com.example.CENG453_20242_GROUP15_backend.leaderboard;

public class LeaderboardDTO {
    private String username;
    private Integer score;

    public LeaderboardDTO(String username, Integer score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
