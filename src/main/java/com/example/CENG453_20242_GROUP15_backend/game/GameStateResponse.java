package com.example.CENG453_20242_GROUP15_backend.game;

import java.util.List;
import java.util.stream.Collectors;

public class GameStateResponse {
    private int currentPlayerId;
    private String currentPlayerName;
    private boolean gameOver;
    private List<String> playerNames;
    private List<Integer> handSizes;
    private List<Card> playerHand; // Only for human player
    private Card topCard;
    private Card.Color currentColor;
    private boolean clockwise;

    public static GameStateResponse from(Game game) {
        GameStateResponse response = new GameStateResponse();
        response.currentPlayerId = game.getCurrentPlayer().getId();
        response.currentPlayerName = game.getCurrentPlayer().getName();
        response.gameOver = game.isGameOver();
        response.playerNames = game.getPlayers().stream().map(Player::getName).collect(Collectors.toList());
        response.handSizes = game.getPlayers().stream().map(p -> p.getHand().size()).collect(Collectors.toList());
        response.playerHand = game.getPlayers().get(0).getHand(); // Human's full hand
        response.topCard = game.getDiscardPile().peek();
        response.currentColor = game.getCurrentColor();
        response.clockwise = game.isClockwise();
        return response;
    }

    // Getters and setters

    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(int currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public String getCurrentPlayerName() {
        return currentPlayerName;
    }

    public void setCurrentPlayerName(String currentPlayerName) {
        this.currentPlayerName = currentPlayerName;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
    }

    public List<Integer> getHandSizes() {
        return handSizes;
    }

    public void setHandSizes(List<Integer> handSizes) {
        this.handSizes = handSizes;
    }

    public List<Card> getPlayerHand() {
        return playerHand;
    }

    public void setPlayerHand(List<Card> playerHand) {
        this.playerHand = playerHand;
    }

    public Card getTopCard() {
        return topCard;
    }

    public void setTopCard(Card topCard) {
        this.topCard = topCard;
    }

    public Card.Color getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(Card.Color currentColor) {
        this.currentColor = currentColor;
    }

    public boolean isClockwise() {
        return clockwise;
    }

    public void setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
    }
}
