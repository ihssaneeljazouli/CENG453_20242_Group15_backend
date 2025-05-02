package com.example.CENG453_20242_GROUP15_backend.game;

import java.util.List;
import java.util.Random;

public class SoloGame {
    private final Game game;
    private final Random random = new Random();
    private int drawTwoStack = 0;

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
        Player cpu = game.getCurrentPlayer();
        List<Card> playable = cpu.getPlayableCards(game.getDiscardPile().peek(), game.getCurrentColor());

        if (playable.isEmpty()) {
            game.drawCard(cpu);
            playable = cpu.getPlayableCards(game.getDiscardPile().peek(), game.getCurrentColor());
        }

        if (!playable.isEmpty()) {
            Card cardToPlay = playable.get(random.nextInt(playable.size()));
            Card.Color chosenColor = cardToPlay.getColor();

            if (cardToPlay.getColor() == Card.Color.WILD) {
                chosenColor = randomColor(); // Randomly choose color if wild
            }

            game.getDiscardPile().push(cardToPlay);
            cpu.getHand().remove(cardToPlay);
            applyCardEffect(cardToPlay, chosenColor);
            game.checkWin(cpu);
        }

        if (!isGameOver()) game.advanceTurn();
    }

    // Applies the effect of special cards
    private void applyCardEffect(Card card, Card.Color chosenColor) {
        switch (card.getType()) {
            case SKIP -> game.advanceTurn();
            case REVERSE -> game.setClockwise(!game.isClockwise());
            case DRAW_TWO -> {
                drawTwoStack += 2;
                handleDrawTwoStack();
            }
            case WILD_DRAW_FOUR -> {
                game.setCurrentColor(chosenColor);
                Player next = game.getCurrentPlayer();
                for (int i = 0; i < 4; i++) game.drawCard(next);
                game.advanceTurn();
            }
            case WILD -> game.setCurrentColor(chosenColor);
            default -> game.setCurrentColor(card.getColor());
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
}
