package com.example.CENG453_20242_GROUP15_backend.game;

import java.util.List;
import java.util.Random;

public class SoloGame {
    private final Game game;
    private final Random random = new Random();
    private int drawTwoStack = 0;
    private Player winner;

    public SoloGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public boolean isGameOver() {
        return game.isGameOver();
    }

    // Player plays a card
    public boolean playPlayerCard(Card card, Card.Color chosenColor) {
        Player player = game.getCurrentPlayer();
        if (!player.getHand().contains(card)) return false;
        if (!card.isPlayableOn(game.getDiscardPile().peek(), game.getCurrentColor())) return false;

        game.getDiscardPile().push(card);
        player.getHand().remove(card);
        applyCardEffect(card, chosenColor);
        game.checkWin(player);
        if (!isGameOver()) game.advanceTurn();
        return true;
    }


    public void playCpuTurn() {
        try {
            Player cpu = game.getCurrentPlayer();
            List<Card> playable = cpu.getPlayableCards(game.getDiscardPile().peek(), game.getCurrentColor());

            if (playable.isEmpty()) {
                game.drawCard(cpu);
                Thread.sleep(800);  // delay to show draw
                playable = cpu.getPlayableCards(game.getDiscardPile().peek(), game.getCurrentColor());
            }

            if (!playable.isEmpty()) {
                Card cardToPlay = playable.get(random.nextInt(playable.size()));
                Card.Color chosenColor = cardToPlay.getColor();

                if (cardToPlay.getColor() == Card.Color.WILD) {
                    chosenColor = randomColor();
                }

                game.getDiscardPile().push(cardToPlay);
                cpu.getHand().remove(cardToPlay);
                applyCardEffect(cardToPlay, chosenColor);
                game.checkWin(cpu);
                Thread.sleep(800); // delay to show card played
            }

            if (!isGameOver()) game.advanceTurn();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    // Applies the effect of special cards
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
                drawTwoStack += 2;
                handleDrawTwoStack();
            }
            case WILD_DRAW_FOUR -> {
                game.setCurrentColor(chosenColor);
                Player next = game.getCurrentPlayer();
                for (int i = 0; i < 4; i++) game.drawCard(next);
                game.advanceTurn();
            }
            case WILD -> {
                game.setCurrentColor(chosenColor);
            }
            case NUMBER -> {
                game.setCurrentColor(card.getColor());
            }
        }
    }


    private void handleDrawTwoStack() {
        Player next = game.getCurrentPlayer();
        List<Card> stackable = next.getPlayableCards(game.getDiscardPile().peek(), game.getCurrentColor()).stream()
                .filter(c -> c.getType() == Card.Type.DRAW_TWO)
                .toList();

        if (!stackable.isEmpty()) {
            Card nextCard = stackable.get(0);
            game.getDiscardPile().push(nextCard);
            next.getHand().remove(nextCard);
            drawTwoStack += 2;
            game.setCurrentColor(nextCard.getColor());
            handleDrawTwoStack();
        } else {
            for (int i = 0; i < drawTwoStack; i++) game.drawCard(next);
            drawTwoStack = 0;
            game.advanceTurn();
        }
    }

    private Card.Color randomColor() {
        Card.Color[] colors = {Card.Color.RED, Card.Color.YELLOW, Card.Color.GREEN, Card.Color.BLUE};
        return colors[random.nextInt(colors.length)];
    }

    public boolean cheat(String action) {
        Card.Color currentColor = game.getCurrentColor();

        Card cheatCard = switch (action.toLowerCase()) {
            case "skip" -> new Card(currentColor, Card.Type.SKIP, -1);
            case "reverse" -> new Card(currentColor, Card.Type.REVERSE, -1);
            case "drawtwo" -> new Card(currentColor, Card.Type.DRAW_TWO, -1);
            case "wild" -> new Card(Card.Color.WILD, Card.Type.WILD, -1);
            case "wilddrawfour" -> new Card(Card.Color.WILD, Card.Type.WILD_DRAW_FOUR, -1);
            default -> null;
        };

        if (cheatCard == null) return false;

        // Use default color (e.g., RED) for wilds
        Card.Color chosenColor;
        if (cheatCard.getType() == Card.Type.WILD || cheatCard.getType() == Card.Type.WILD_DRAW_FOUR) {
            chosenColor = Card.Color.RED; // default for wilds
        } else {
            chosenColor = cheatCard.getColor();
        }

        applyCardEffect(cheatCard, chosenColor);
        return true;
    }
    public Player getWinner(){
        return game.getWinner();
    }

}
