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
        Map<String, List<String>> moves = gameState.getMoves();
        int i = (int) (Math.random() * moves.values().stream().mapToInt(List::size).sum());
        for (String start : moves.keySet()) {
            if (i >= moves.get(start).size()) i -= moves.get(start).size();
            else {
                gameState.move(start, moves.get(start).get(i));
                break;
            }
        }
        return gameState.toString();
    }

    @GetMapping("/moves/{state}")
    public Map<String, List<String>> moves(@PathVariable String state) {
        return new GameState(state).getMoves();
    }

    @GetMapping("/move/{state}/{start}/{end}")
    public String move(@PathVariable String state, @PathVariable String start, @PathVariable String end) {
        GameState gameState = new GameState(state);
        gameState.move(start, end);
        return gameState.toString();
    }

    @GetMapping("/start")
    public String start() {
        return new GameState().toString();
    }
}
