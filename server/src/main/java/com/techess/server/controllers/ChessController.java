package com.techess.server.controllers;

import java.util.Map;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.techess.server.controllers.models.GameState;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api")
public class ChessController {
    @GetMapping("/compute/{state}")
    public String compute(@PathVariable String state) {
        GameState gameState = new GameState(state);
        String turn = state.split(" ")[1];
        Map<String, List<String>> moves = gameState.getMoves();
        String start = "";
        String end = "";
        int maxScore = Integer.MIN_VALUE;
        for (String s : moves.keySet()) {
            for (String e : moves.get(s)) {
                GameState potential = new GameState(gameState);
                potential.move(s, e);
                int score = 0;
                for (int i = 0; i < 100; i++) {
                    String result = potential.simulateGame();
                    if (result.equals(turn)) score++;
                    else if (result.equals("w") || result.equals("b")) score--;
                }
                if (score > maxScore) {
                    start = s;
                    end = e;
                    maxScore = score;
                }
            }
        }
        gameState.move(start, end);
        String status = gameState.gameStatus();
        return (status.equals("") ? "" : gameState.gameStatus() + " ") + gameState.toString();
    }

    @GetMapping("/moves/{state}")
    public Map<String, List<String>> moves(@PathVariable String state) {
        return new GameState(state).getMoves();
    }

    @GetMapping("/move/{state}/{start}/{end}")
    public String move(@PathVariable String state, @PathVariable String start, @PathVariable String end) {
        GameState gameState = new GameState(state);
        gameState.move(start, end);
        String status = gameState.gameStatus();
        return (status.equals("") ? "" : gameState.gameStatus() + " ") + gameState.toString();
    }

    @GetMapping("/start")
    public String start() {
        return new GameState().toString();
    }
}
