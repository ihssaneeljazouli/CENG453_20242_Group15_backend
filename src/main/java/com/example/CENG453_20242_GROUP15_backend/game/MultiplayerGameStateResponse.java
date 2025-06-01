package com.example.CENG453_20242_GROUP15_backend.game;

import java.util.List;


public class MultiplayerGameStateResponse {

    private List<String> players;
    private String currentTurn;
    private boolean gameStarted;
    private String opponentName;
    private int opponentHandSize;
    private Card topCard;
    private String currentColor;
    private String directionSymbol;
    private String playerName;
    private List<Card> playerHand;
    private boolean choosingColor;



    public static MultiplayerGameStateResponse from(MultiplayerGame game, String playerName) {
        MultiplayerGameStateResponse response = new MultiplayerGameStateResponse();

        Game g = game.getGame();

        response.setPlayers(game.getPlayers());
        response.setCurrentTurn(g.getCurrentPlayer().getName());
        response.setGameStarted(game.getPlayers().size() == 2);

        // Set opponent info:
        String opponent = game.getPlayers().stream()
                .filter(p -> !p.equals(playerName))
                .findFirst()
                .orElse("Waiting...");

        response.setOpponentName(opponent);

        Player opponentPlayer = g.getPlayers().stream()
                .filter(p -> p.getName().equals(opponent))
                .findFirst()
                .orElse(null);

        response.setOpponentHandSize(opponentPlayer != null ? opponentPlayer.getHand().size() : 0);

        // Set player info:
        response.setPlayerName(playerName);

        Player player = g.getPlayers().stream()
                .filter(p -> p.getName().equals(playerName))
                .findFirst()
                .orElseThrow();

        response.setPlayerHand(player.getHand());

        // Set game status:
        response.setTopCard(g.getDiscardPile().peek());
        response.setCurrentColor(g.getCurrentColor().name());
        response.setDirectionSymbol(g.isClockwise() ? "→" : "←");

        // Challenge / choosing color:
        response.setChoosingColor(game.isChoosingColor() || game.isChallengePending());

        return response;
    }

    // === Getters and setters ===
    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public int getOpponentHandSize() {
        return opponentHandSize;
    }

    public void setOpponentHandSize(int opponentHandSize) {
        this.opponentHandSize = opponentHandSize;
    }

    public Card getTopCard() {
        return topCard;
    }

    public void setTopCard(Card topCard) {
        this.topCard = topCard;
    }

    public String getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(String currentColor) {
        this.currentColor = currentColor;
    }

    public String getDirectionSymbol() {
        return directionSymbol;
    }

    public void setDirectionSymbol(String directionSymbol) {
        this.directionSymbol = directionSymbol;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public List<Card> getPlayerHand() {
        return playerHand;
    }

    public void setPlayerHand(List<Card> playerHand) {
        this.playerHand = playerHand;
    }

    public boolean isChoosingColor() {
        return choosingColor;
    }

    public void setChoosingColor(boolean choosingColor) {
        this.choosingColor = choosingColor;
    }
}