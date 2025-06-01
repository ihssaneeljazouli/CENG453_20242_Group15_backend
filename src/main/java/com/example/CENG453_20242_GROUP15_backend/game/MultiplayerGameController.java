package com.example.CENG453_20242_GROUP15_backend.game;

import com.example.CENG453_20242_GROUP15_backend.exception.UnauthorizedException;
import com.example.CENG453_20242_GROUP15_backend.user.UserEntity;
import com.example.CENG453_20242_GROUP15_backend.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RestController
@RequestMapping("/multiplayer")
@CrossOrigin
public class MultiplayerGameController {

    private final Map<String, MultiplayerGame> multiplayerGames = new ConcurrentHashMap<>();

    @Autowired
    private UserService userService;

    private UserEntity getAuthenticatedUser(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            throw new UnauthorizedException("User not authenticated.");
        }
        return user;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createGame(HttpSession session) {
        UserEntity user = getAuthenticatedUser(session);

        String gameId = UUID.randomUUID().toString().substring(0, 6);
        MultiplayerGame game = new MultiplayerGame(gameId, user.getUsername());
        multiplayerGames.put(gameId, game);

        return ResponseEntity.ok(gameId);
    }

    @PostMapping("/join/{gameId}")
    public ResponseEntity<?> joinGame(@PathVariable String gameId, HttpSession session) {
        UserEntity user = getAuthenticatedUser(session);

        MultiplayerGame game = multiplayerGames.get(gameId);
        if (game == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game not found.");
        }

        boolean joined = game.addPlayer(user.getUsername());
        if (!joined) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to join game.");
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/state/{gameId}")
    public ResponseEntity<?> getGameState(@PathVariable String gameId, HttpSession session) {
        UserEntity user = getAuthenticatedUser(session);

        MultiplayerGame game = multiplayerGames.get(gameId);
        if (game == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game not found.");
        }

        return ResponseEntity.ok(MultiplayerGameStateResponse.from(game, user.getUsername()));
    }

    @PostMapping("/play/{gameId}")
    public ResponseEntity<?> playCard(@PathVariable String gameId, @RequestBody PlayCardRequest request, HttpSession session) {
        UserEntity user = getAuthenticatedUser(session);

        MultiplayerGame game = multiplayerGames.get(gameId);
        if (game == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game not found.");
        }

        boolean success = game.playPlayerCard(user.getUsername(), request.getCardIndex(), request.getChosenColor());
        if (!success) {
            return ResponseEntity.badRequest().body("Invalid move.");
        }

        return ResponseEntity.ok(MultiplayerGameStateResponse.from(game, user.getUsername()));
    }

    @PostMapping("/draw/{gameId}")
    public ResponseEntity<?> drawCard(@PathVariable String gameId, HttpSession session) {
        UserEntity user = getAuthenticatedUser(session);

        MultiplayerGame game = multiplayerGames.get(gameId);
        if (game == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game not found.");
        }

        boolean success = game.drawCard(user.getUsername());
        if (!success) {
            return ResponseEntity.badRequest().body("Cannot draw card.");
        }

        return ResponseEntity.ok(MultiplayerGameStateResponse.from(game, user.getUsername()));
    }

    @PostMapping("/challenge/{gameId}")
    public ResponseEntity<?> challenge(@PathVariable String gameId, HttpSession session) {
        UserEntity user = getAuthenticatedUser(session);

        MultiplayerGame game = multiplayerGames.get(gameId);
        if (game == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game not found.");
        }

        game.resolveChallenge(user.getUsername());

        return ResponseEntity.ok(MultiplayerGameStateResponse.from(game, user.getUsername()));
    }

    @PostMapping("/choose-color/{gameId}")
    public ResponseEntity<?> chooseColor(@PathVariable String gameId, @RequestBody Map<String, String> payload, HttpSession session) {
        UserEntity user = getAuthenticatedUser(session);

        MultiplayerGame game = multiplayerGames.get(gameId);
        if (game == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game not found.");
        }

        String color = payload.get("color");
        game.chooseColor(user.getUsername(), color);

        return ResponseEntity.ok(MultiplayerGameStateResponse.from(game, user.getUsername()));
    }
}

