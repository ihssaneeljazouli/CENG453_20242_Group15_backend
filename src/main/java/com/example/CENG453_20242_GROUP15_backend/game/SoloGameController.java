package com.example.CENG453_20242_GROUP15_backend.game;

import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solo-game")
@CrossOrigin
public class SoloGameController {

    private SoloGame soloGame;

    @PostConstruct
    public void initGame() {
        List<Player> players = List.of(
                new Player(1, "You", false),
                new Player(2, "CPU1", true),
                new Player(3, "CPU2", true),
                new Player(4, "CPU3", true)
        );
        Game game = new Game(players);
        this.soloGame = new SoloGame(game);
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
        return ResponseEntity.ok(GameStateResponse.from(soloGame.getGame()));
    }

    @PostMapping("/draw")
    public GameStateResponse drawCard() {
        soloGame.getGame().drawCard(soloGame.getGame().getCurrentPlayer());
        return GameStateResponse.from(soloGame.getGame());
    }

    @PostMapping("/cpu-turn")
    public GameStateResponse cpuTurn() {
        soloGame.playCpuTurn();
        return GameStateResponse.from(soloGame.getGame());
    }

    @PostMapping("/restart")
    public GameStateResponse restartGame() {
        initGame();
        return GameStateResponse.from(soloGame.getGame());
    }

}
