package com.techess.server.controllers;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.techess.server.controllers.models.GameState;
import com.techess.server.controllers.models.Move;
import com.techess.server.controllers.models.MoveGenerator;
import com.techess.server.controllers.models.Simulator;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api")
public class ChessController {

    @GetMapping("/compute")
    public String compute(@RequestParam String state) {
        GameState gameState = new GameState(state);
        char turn = gameState.getTurn();
        List<Move> moves = MoveGenerator.getMoves(gameState);
        Move move = null;
        int maxScore = Integer.MIN_VALUE;
        for (Move possibility : moves) {
            GameState potential = new GameState(gameState);
            Move.apply(potential, possibility);
            int score = 0;
            for (int i = 0; i < 100; i++) {
                char result = Simulator.simulateGame(potential);
                if (result == turn) score++;
                else if (result == 'w' || result == 'b') score--;
            }
            if (score > maxScore) {
                move = possibility;
                maxScore = score;
            }
        }
        Move.apply(gameState, move);
        char status = gameState.getStatus();
        return (status == '-' ? "" : status + " ") + gameState.toString();
    }

    @GetMapping("/moves")
    public Map<String, List<String>> moves(@RequestParam String state) {
        Map<String, List<String>> result = new HashMap<>();
        List<Move> moves = MoveGenerator.getMoves(new GameState(state));
        for (Move move : moves) {
            if (!result.containsKey(move.getFrom())) result.put(move.getFrom(), new ArrayList<>());
            result.get(move.getFrom()).add(move.getTo());
        }
        return result;
    }

    @GetMapping("/move")
    public String move(@RequestParam String state, @RequestParam String from, @RequestParam String to) {
        GameState gameState = new GameState(state);
        Move.apply(gameState, new Move(from, to));
        char status = gameState.getStatus();
        return (status == '-' ? "" : gameState.getStatus() + " ") + gameState.toString();
    }

    @GetMapping("/start")
    public String start() {
        return new GameState().toString();
    }
    
}
