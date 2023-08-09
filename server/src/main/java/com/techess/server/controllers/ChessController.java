package com.techess.server.controllers;

import java.util.Map;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.techess.server.controllers.models.GameState;

@RestController
@RequestMapping("/api")
public class ChessController {
    @GetMapping("/compute/{state}")
    public String compute(@PathVariable String state) {
        return "compute" + state;
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
