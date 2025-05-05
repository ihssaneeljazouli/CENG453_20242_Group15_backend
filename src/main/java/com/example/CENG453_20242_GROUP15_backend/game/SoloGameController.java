package com.example.CENG453_20242_GROUP15_backend.game;

import com.example.CENG453_20242_GROUP15_backend.exception.UnauthorizedException;
import com.example.CENG453_20242_GROUP15_backend.user.UserEntity;
import com.example.CENG453_20242_GROUP15_backend.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/solo")
@CrossOrigin
public class SoloGameController {

    private final Map<String, SoloGame> soloGames = new ConcurrentHashMap<>();

    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession session;

    // Helper method to check if the user is authenticated
    private UserEntity getAuthenticatedUser() {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            throw new UnauthorizedException("User not authenticated.");
        }
        return user;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startGame() {
        UserEntity user = getAuthenticatedUser();
        List<Player> players = List.of(
                new Player(user),
                new Player("CPU1", true),
                new Player("CPU2", true),
                new Player("CPU3", true)
        );

        Game game = new Game(players);
        SoloGame soloGame = new SoloGame(game);
        soloGames.put(user.getUsername(), soloGame);

        return ResponseEntity.ok("Solo game started.");
    }

    @GetMapping("/state")
    public ResponseEntity<?> getGameState() {
        UserEntity user = getAuthenticatedUser();

        SoloGame soloGame = soloGames.get(user.getUsername());
        if (soloGame == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game not started.");
        }

        return ResponseEntity.ok(GameStateResponse.from(soloGame.getGame()));
    }

    @PostMapping("/play")
    public ResponseEntity<?> playCard(@RequestBody PlayCardRequest request) {
        UserEntity user = getAuthenticatedUser();

        SoloGame soloGame = soloGames.get(user.getUsername());
        if (soloGame == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game not started.");
        }

        Game game = soloGame.getGame();
        Player current = game.getCurrentPlayer();

        if (current.isCPU()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("It's not your turn.");
        }

        List<Card> hand = current.getHand();
        if (request.getCardIndex() < 0 || request.getCardIndex() >= hand.size()) {
            return ResponseEntity.badRequest().body("Invalid card index");
        }

        Card cardToPlay = hand.get(request.getCardIndex());
        Card.Color chosenColor = request.getChosenColor();

        boolean success = soloGame.playPlayerCard(cardToPlay, chosenColor);
        if (!success) return ResponseEntity.badRequest().body("Invalid move");

        // Simulate CPU turns after the player's move
        while (!soloGame.isGameOver() && !game.getCurrentPlayer().equals(current)) {
            soloGame.playCpuTurn();
        }

        if (soloGame.isGameOver()) {
            Player winner = soloGame.getWinner();
            if (!winner.isCPU() && winner.getUser() != null) {
                userService.updateUserScore(winner.getUser().getId(), 1); // Reward winner
            } else {
                userService.updateUserScore(current.getUser().getId(), -1); // Human player lost
            }
        }

        return ResponseEntity.ok(GameStateResponse.from(game));
    }

    @PostMapping("/draw")
    public ResponseEntity<?> drawCard() {
        UserEntity user = getAuthenticatedUser();

        SoloGame soloGame = soloGames.get(user.getUsername());
        if (soloGame == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game not started.");
        }

        soloGame.getGame().drawCard(soloGame.getGame().getCurrentPlayer());
        return ResponseEntity.ok(GameStateResponse.from(soloGame.getGame()));
    }

    @PostMapping("/restart")
    public ResponseEntity<?> restartGame() {
        UserEntity user = getAuthenticatedUser();

        // Restart the game
        ResponseEntity<String> startResponse = startGame();
        if (!startResponse.getStatusCode().is2xxSuccessful()) {
            return startResponse;
        }

        return ResponseEntity.ok(GameStateResponse.from(soloGames.get(user.getUsername()).getGame()));
    }

    @PostMapping("/cheat/{action}")
    public ResponseEntity<?> cheatPlay(@PathVariable String action) {
        UserEntity user = getAuthenticatedUser();

        SoloGame soloGame = soloGames.get(user.getUsername());
        if (soloGame == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game not started.");
        }

        boolean success = soloGame.cheat(action);
        if (!success) {
            return ResponseEntity.badRequest().body("Invalid cheat action");
        }

        return ResponseEntity.ok(GameStateResponse.from(soloGame.getGame()));
    }



}
