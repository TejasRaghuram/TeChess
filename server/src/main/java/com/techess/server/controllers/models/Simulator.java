package com.techess.server.controllers.models;

import java.util.List;

public class Simulator {

    public static char simulateGame(GameState state) {
        GameState simulation = new GameState(state);
        char status = simulation.getStatus();
        while (status == '-') {
            List<Move> moves = MoveGenerator.getMoves(simulation);
            Move.apply(simulation, moves.get((int) (Math.random() * moves.size())));
            status = simulation.getStatus();
        }
        return status;
    }
    
}
