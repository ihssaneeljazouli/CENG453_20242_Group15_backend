package com.example.CENG453_20242_GROUP15_backend.game;

import com.example.CENG453_20242_GROUP15_backend.user.UserEntity;
import com.example.CENG453_20242_GROUP15_backend.user.UserService;
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



    @PostMapping("/start")
    public ResponseEntity<String> startGame() {
        Optional<UserEntity> optionalUser = userService.getCurrentUser();
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }

        UserEntity user = optionalUser.get();
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
        String username = userService.getCurrentUsername();
        SoloGame soloGame = soloGames.get(username);
        if (soloGame == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game not started.");
        }
        return ResponseEntity.ok(GameStateResponse.from(soloGame.getGame()));
    }

    @PostMapping("/play")
    public ResponseEntity<?> playCard(@RequestBody PlayCardRequest request) {
        String username = userService.getCurrentUsername();
        SoloGame soloGame = soloGames.get(username);
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

        while (!soloGame.isGameOver() && !game.getCurrentPlayer().equals(current)) {
            soloGame.playCpuTurn();
        }

        if (soloGame.isGameOver()) {
            Player winner = soloGame.getWinner();
            if (!winner.isCPU() && winner.getUser() != null) {
                userService.updateUserScore(winner.getUser().getId(), 1); // reward winner
            } else {
                userService.updateUserScore(current.getUser().getId(), -1); // human player lost
            }
        }


        return ResponseEntity.ok(GameStateResponse.from(game));
    }

    @PostMapping("/draw")
    public ResponseEntity<?> drawCard() {
        String username = userService.getCurrentUsername();
        SoloGame soloGame = soloGames.get(username);
        if (soloGame == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game not started.");
        }

        soloGame.getGame().drawCard(soloGame.getGame().getCurrentPlayer());
        return ResponseEntity.ok(GameStateResponse.from(soloGame.getGame()));
    }

    @PostMapping("/restart")
    public ResponseEntity<?> restartGame() {
        ResponseEntity<String> startResponse = startGame();
        if (!startResponse.getStatusCode().is2xxSuccessful()) {
            return startResponse;
        }

        String username = userService.getCurrentUsername();
        return ResponseEntity.ok(GameStateResponse.from(soloGames.get(username).getGame()));
    }

    @PostMapping("/cheat/{action}")
    public ResponseEntity<?> cheatPlay(@PathVariable String action) {
        String username = userService.getCurrentUsername();
        SoloGame soloGame = soloGames.get(username);
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
