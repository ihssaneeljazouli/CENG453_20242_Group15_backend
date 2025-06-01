package com.example.CENG453_20242_GROUP15_backend.game;

import com.example.CENG453_20242_GROUP15_backend.game.Card;

import java.util.*;

public class MultiplayerGame {

    private final String gameId;
    private Game game;
    private final Map<String, Player> playerMap = new HashMap<>();
    private final Random random = new Random();

    private boolean challengePending = false;
    private boolean choosingColor = false;
    private String playerToChooseColor = null;
    private Player winner;

    public MultiplayerGame(String gameId, String hostPlayer) {
        this.gameId = gameId;
        this.game = null; // will initialize after both players joined
        addPlayer(hostPlayer);
    }

    public String getGameId() {
        return gameId;
    }

    public Game getGame() {
        return game;
    }

    public boolean isGameOver() {
        return game != null && game.isGameOver();
    }

    public List<String> getPlayers() {
        return new ArrayList<>(playerMap.keySet());
    }

    public boolean addPlayer(String playerName) {
        if (playerMap.size() >= 2) return false;

        Player player = new Player(playerName, false);
        playerMap.put(playerName, player);

        if (playerMap.size() == 2) {
            List<Player> playerList = new ArrayList<>(playerMap.values());
            game = new Game(playerList);
        }

        return true;
    }

    public boolean playPlayerCard(String playerName, int cardIndex, Card.Color chosenColor) {
        if (game == null || !isPlayerTurn(playerName)) return false;

        Player player = playerMap.get(playerName);
        List<Card> hand = player.getHand();

        if (cardIndex < 0 || cardIndex >= hand.size()) return false;

        Card card = hand.get(cardIndex);

        if (!card.isPlayableOn(game.getDiscardPile().peek(), game.getCurrentColor())) {
            return false;
        }

        game.getDiscardPile().push(card);
        hand.remove(card);

        applyCardEffect(card, chosenColor);

        game.checkWin(player);
        if (isGameOver()) {
            winner = player;
            return true;
        }

        game.advanceTurn();
        return true;
    }

    public boolean drawCard(String playerName) {
        if (game == null || !isPlayerTurn(playerName)) return false;

        Player player = playerMap.get(playerName);
        List<Card> playable = player.getPlayableCards(game.getDiscardPile().peek(), game.getCurrentColor());

        if (!playable.isEmpty()) return false; // Can't draw if you have playable cards

        game.drawCard(player);

        List<Card> newPlayable = player.getPlayableCards(game.getDiscardPile().peek(), game.getCurrentColor());

        if (newPlayable.isEmpty()) {
            game.advanceTurn();
        }

        return true;
    }

    private void applyCardEffect(Card card, Card.Color chosenColor) {
        switch (card.getType()) {
            case SKIP -> {
                game.setCurrentColor(card.getColor());
                game.advanceTurn();
            }
            case REVERSE -> {
                game.setCurrentColor(card.getColor());
                game.setClockwise(!game.isClockwise());
            }
            case DRAW_TWO -> {
                game.setCurrentColor(card.getColor());
                Player next = game.getCurrentPlayer();
                for (int i = 0; i < 2; i++) game.drawCard(next);
                game.advanceTurn();
            }
            case WILD_DRAW_FOUR -> {
                challengePending = true;
                playerToChooseColor = game.getCurrentPlayer().getName(); // The player who played WDF will choose color after challenge
                game.advanceTurn(); // Opponent can now challenge
            }
            case WILD -> {
                choosingColor = true;
                playerToChooseColor = game.getCurrentPlayer().getName();
            }
            case NUMBER -> {
                game.setCurrentColor(card.getColor());
            }
        }
    }

    public void resolveChallenge(String challengerName) {
        if (game == null || !challengePending) return;

        Player challengedPlayer = getPreviousPlayer();
        Card.Color prevColor = game.getCurrentColor();

        boolean validChallenge = challengedPlayer.getHand().stream()
                .anyMatch(c -> c.getColor() == prevColor && c.isPlayableOn(game.getDiscardPile().peek(), prevColor));

        if (validChallenge) {
            // Challenged player draws 4
            for (int i = 0; i < 4; i++) game.drawCard(challengedPlayer);
        } else {
            // Challenger draws 6
            Player challenger = playerMap.get(challengerName);
            for (int i = 0; i < 6; i++) game.drawCard(challenger);
        }

        challengePending = false;
    }

    public void chooseColor(String playerName, String colorStr) {
        if (game == null) return;
        if (!choosingColor && !challengePending) return;
        if (!playerName.equals(playerToChooseColor)) return;

        Card.Color chosenColor = Card.Color.valueOf(colorStr);
        game.setCurrentColor(chosenColor);

        choosingColor = false;
        playerToChooseColor = null;
    }

    public boolean isPlayerTurn(String playerName) {
        return game != null && game.getCurrentPlayer().getName().equals(playerName);
    }

    public boolean isChallengePending() {
        return challengePending;
    }

    public boolean isChoosingColor() {
        return choosingColor;
    }

    public String getPlayerToChooseColor() {
        return playerToChooseColor;
    }

    public Player getWinner() {
        return winner;
    }

    private Player getPreviousPlayer() {
        if (game == null) return null;

        int index;
        if (game.isClockwise()) {
            index = (game.getCurrentPlayerIndex() - 1 + game.getPlayers().size()) % game.getPlayers().size();
        } else {
            index = (game.getCurrentPlayerIndex() + 1) % game.getPlayers().size();
        }

        return game.getPlayers().get(index);
    }
}
