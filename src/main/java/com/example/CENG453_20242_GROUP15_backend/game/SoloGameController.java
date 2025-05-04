package com.example.CENG453_20242_GROUP15_backend.game;

import ch.qos.logback.classic.Logger;
import com.example.CENG453_20242_GROUP15_backend.user.UserEntity;
import com.example.CENG453_20242_GROUP15_backend.user.UserRepository;
import com.example.CENG453_20242_GROUP15_backend.user.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/solo")
@CrossOrigin
public class SoloGameController {

    private SoloGame soloGame;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;


    @PostMapping("/start")
    public ResponseEntity<String> startGame() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            UserEntity user = optionalUser.get();

            List<Player> players = List.of(
                    new Player(user.getId(), user.getUsername(), false),
                    new Player("CPU1", true),
                    new Player("CPU2", true),
                    new Player("CPU3", true)
            );

            Game game = new Game(players);
            this.soloGame = new SoloGame(game);

            return ResponseEntity.ok("Solo game started.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }
    }




    @GetMapping("/state")
    public GameStateResponse getGameState() {
        return GameStateResponse.from(soloGame.getGame());
    }

    @PostMapping("/play")
    public ResponseEntity<?> playCard(@RequestBody PlayCardRequest request) {
        Player current = soloGame.getGame().getCurrentPlayer();
        Card cardToPlay = current.getHand().get(request.getCardIndex());

        Card.Color chosenColor = request.getChosenColor();
        boolean success = soloGame.playPlayerCard(cardToPlay, chosenColor);

        if (!success) return ResponseEntity.badRequest().body("Invalid move");

        // Let CPUs take their turns as long as the game isn't over
        while (!soloGame.isGameOver() && !soloGame.getGame().getCurrentPlayer().equals(current)) {
            soloGame.playCpuTurn();
        }

        // Update leaderboard on game end
        if (soloGame.isGameOver()) {
            boolean playerWon = soloGame.getWinner().equals(current);

            // Ensure the current player is human (not CPU)
            if (!current.isCPU()) {
                Integer id = current.getId();  // Get the human player's ID

                if (playerWon) {
                    userService.updateUserScore(id, 1);  // Update score for win
                } else {
                    userService.updateUserScore(id, -1);  // Update score for loss
                }
            }
        }


        return ResponseEntity.ok(GameStateResponse.from(soloGame.getGame()));
    }

    @PostMapping("/draw")
    public GameStateResponse drawCard() {
        soloGame.getGame().drawCard(soloGame.getGame().getCurrentPlayer());
        return GameStateResponse.from(soloGame.getGame());
    }


    @PostMapping("/restart")
    public GameStateResponse restartGame() {
        startGame();
        return GameStateResponse.from(soloGame.getGame());
    }

    @PostMapping("/cheat/{action}")
    public ResponseEntity<?> cheatPlay(@PathVariable String action) {
        boolean success = soloGame.cheat(action);

        if (!success) {
            return ResponseEntity.badRequest().body("Invalid cheat action");
        }
        return ResponseEntity.ok(GameStateResponse.from(soloGame.getGame()));
    }
}
