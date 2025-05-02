package com.example.CENG453_20242_GROUP15_backend.game;

import java.util.*;

public class Game {
    private List<Player> players;
    private Deque<Card> drawPile;
    private Deque<Card> discardPile;
    private int currentPlayerIndex = 0;
    private boolean isClockwise = true;
    private Card.Color currentColor;
    private boolean gameOver = false;

    public Game(List<Player> players) {
        this.players = players;
        this.drawPile = generateDeck();
        this.discardPile = new ArrayDeque<>();
        startGame();
    }

    private Deque<Card> generateDeck() {
        List<Card> deck = new ArrayList<>();
        for (Card.Color color : Card.Color.values()) {
            if (color == Card.Color.WILD) continue;
            for (int i = 0; i <= 9; i++) {
                deck.add(new Card(color, Card.Type.NUMBER, i));
                if (i != 0) deck.add(new Card(color, Card.Type.NUMBER, i));
            }
            for (int i = 0; i < 2; i++) {
                deck.add(new Card(color, Card.Type.SKIP, -1));
                deck.add(new Card(color, Card.Type.REVERSE, -1));
                deck.add(new Card(color, Card.Type.DRAW_TWO, -1));
            }
        }
        for (int i = 0; i < 4; i++) {
            deck.add(new Card(Card.Color.WILD, Card.Type.WILD, -1));
            deck.add(new Card(Card.Color.WILD, Card.Type.WILD_DRAW_FOUR, -1));
        }
        Collections.shuffle(deck);
        return new ArrayDeque<>(deck);
    }

    private void startGame() {
        for (Player p : players) {
            for (int i = 0; i < 7; i++) {
                p.getHand().add(drawPile.pop());
            }
        }
        Card first;
        do {
            first = drawPile.pop();
            discardPile.push(first);
        } while (first.getType() == Card.Type.WILD_DRAW_FOUR);
        currentColor = first.getColor();
    }

    public void reshuffleDiscardIntoDraw() {
        Card top = discardPile.pop();
        List<Card> rest = new ArrayList<>(discardPile);
        Collections.shuffle(rest);
        drawPile = new ArrayDeque<>(rest);
        discardPile.clear();
        discardPile.push(top);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void advanceTurn() {
        if (isClockwise) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } else {
            currentPlayerIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
        }
    }

    public void drawCard(Player player) {
        if (drawPile.isEmpty()) reshuffleDiscardIntoDraw();
        if (!drawPile.isEmpty()) {
            player.getHand().add(drawPile.pop());
        }
    }

    public void checkWin(Player player) {
        if (player.getHand().isEmpty()) {
            setGameOver(true);
        }
    }

    // Getters and setters

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Deque<Card> getDrawPile() {
        return drawPile;
    }

    public void setDrawPile(Deque<Card> drawPile) {
        this.drawPile = drawPile;
    }

    public Deque<Card> getDiscardPile() {
        return discardPile;
    }

    public void setDiscardPile(Deque<Card> discardPile) {
        this.discardPile = discardPile;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public boolean isClockwise() {
        return isClockwise;
    }

    public void setClockwise(boolean clockwise) {
        isClockwise = clockwise;
    }

    public Card.Color getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(Card.Color currentColor) {
        this.currentColor = currentColor;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}
