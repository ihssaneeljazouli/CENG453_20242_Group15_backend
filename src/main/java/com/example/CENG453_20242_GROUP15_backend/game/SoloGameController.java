package com.example.CENG453_20242_GROUP15_backend.game;

import com.example.CENG453_20242_GROUP15_backend.user.UserEntity;
import com.example.CENG453_20242_GROUP15_backend.user.UserRepository;
import com.example.CENG453_20242_GROUP15_backend.user.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostConstruct
    public void initGame() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            // Handle the case where authentication is missing
            throw new IllegalStateException("Authentication is required but not available.");
        }
        if (authentication != null) {
            String username = authentication.getName();
            Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

            if (optionalUser.isEmpty()) {
                throw new RuntimeException("Authenticated user not found in the database");
            }

            UserEntity user = optionalUser.get();

            List<Player> players = List.of(
                    new Player(user.getId(), user.getUsername(), false),  // Real user
                    new Player("CPU1", true),
                    new Player("CPU2", true),
                    new Player("CPU3", true)
            );
            Game game = new Game(players);
            this.soloGame = new SoloGame(game);
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
        initGame();
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
