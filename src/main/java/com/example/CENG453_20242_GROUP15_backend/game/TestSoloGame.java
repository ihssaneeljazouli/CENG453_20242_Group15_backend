package com.example.CENG453_20242_GROUP15_backend.game;

import java.util.List;
import java.util.Scanner;

public class TestSoloGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Create players
        Player human = new Player(1,"You",false);
        Player cpu1 = new Player(2,"CPU1",true);
        Player cpu2 = new Player(3,"CPU2",true);
        Player cpu3 = new Player(4,"CPU3",true);

        List<Player> players = List.of(human, cpu1, cpu2, cpu3);
        Game game = new Game(players);
        SoloGame soloGame = new SoloGame(game);

        System.out.println("=== UNO Game Started ===");

        while (!soloGame.isGameOver()) {
            Player current = game.getCurrentPlayer();
            Card topCard = game.getDiscardPile().peek();

            System.out.println("\n--------------------------");
            System.out.println("Current Player: " + current.getName());
            System.out.println("Top Card: " + cardToString(topCard));
            System.out.println("Current Color: " + game.getCurrentColor());
            System.out.println("--------------------------");

            if (current == human) {
                // Display hand
                List<Card> hand = human.getHand();
                System.out.println("Your hand:");
                for (int i = 0; i < hand.size(); i++) {
                    System.out.println("[" + i + "] " + cardToString(hand.get(i)));
                }

                List<Card> playable = human.getPlayableCards(topCard, game.getCurrentColor());

                if (playable.isEmpty()) {
                    System.out.println("No playable cards. Drawing a card...");
                    game.drawCard(current);
                    continue;
                }

                System.out.print("Enter card index to play or type 'draw' to draw a card: ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("draw")) {
                    game.drawCard(current);

                    // Check if the drawn card is playable
                    Card drawnCard = current.getHand().get(current.getHand().size() - 1);
                    if (drawnCard.isPlayableOn(topCard, game.getCurrentColor())) {
                        System.out.println("You drew a playable card: " + cardToString(drawnCard));
                        System.out.print("Do you want to play it? (yes/no): ");
                        String response = scanner.nextLine().trim().toLowerCase();
                        if (response.equals("yes")) {
                            Card.Color chosenColor = null;
                            if (drawnCard.getColor() == Card.Color.WILD) {
                                System.out.print("Choose a color (RED, GREEN, BLUE, YELLOW): ");
                                try {
                                    chosenColor = Card.Color.valueOf(scanner.nextLine().trim().toUpperCase());
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid color. Skipping play.");
                                    game.advanceTurn(); // ensure turn moves
                                    continue;
                                }
                            }
                            soloGame.playPlayerCard(drawnCard, chosenColor);
                        }
                    }

                    // End turn after draw (whether played or not)
                    if (!soloGame.isGameOver()) {
                        game.advanceTurn();
                    }
                    continue;
                }


                try {
                    int cardIndex = Integer.parseInt(input);
                    if (cardIndex < 0 || cardIndex >= hand.size()) {
                        System.out.println("Invalid index.");
                        continue;
                    }

                    Card selected = hand.get(cardIndex);
                    Card.Color chosenColor = null;

                    if (selected.getColor() == Card.Color.WILD) {
                        System.out.print("Choose a color (RED, GREEN, BLUE, YELLOW): ");
                        try {
                            chosenColor = Card.Color.valueOf(scanner.nextLine().trim().toUpperCase());
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid color.");
                            continue;
                        }
                    }

                    boolean success = soloGame.playPlayerCard(selected, chosenColor);
                    if (!success) {
                        System.out.println("Invalid move. Try again.");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }

            } else {
                System.out.println(current.getName() + " is playing...");
                soloGame.playCpuTurn();
            }
        }

        Player winner = game.getCurrentPlayer();
        System.out.println("\n🎉 Game Over! Winner: " + winner.getName());
    }

    // Helper to print card details
    private static String cardToString(Card card) {
        return "[" + card.getColor() + " " + card.getType() + " " + card.getNumber() +  "]";
    }
}
