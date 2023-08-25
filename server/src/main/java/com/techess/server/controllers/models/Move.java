package com.techess.server.controllers.models;

import com.techess.server.controllers.models.GameState.Piece;

public class Move {

    final int from;
    final int to;

    public Move(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public Move(String from, String to) {
        this(getIndex(from), getIndex(to));
    }

    public static void apply(GameState state, Move move) {
        if (state.board[move.from] == Piece.WHITE_PAWN 
            || state.board[move.from] == Piece.BLACK_PAWN 
            || state.board[move.to] != Piece.EMPTY) state.halfMoveClock = 0;
        else state.halfMoveClock++;

        state.board[move.to] = state.board[move.from];
        state.board[move.from] = Piece.EMPTY;

        state.turn = state.turn == 'w' ? 'b' : 'w';

        if (state.turn == 'w') state.fullMoves++;
    }

    private static int getIndex(String square) {
        return getIndex(square.charAt(1) - '1', square.charAt(0) - 'a');
    }

    public static int getIndex(int rank, int file) {
        return 8 * rank + file;
    }

    public String getFrom() {
        return getSquare(from);
    }

    public String getTo() {
        return getSquare(to);
    }

    private static String getSquare(int index) {
        int rank = index / 8;
        int file = index % 8;
        return Character.toString('a' + file) + Integer.toString(rank + 1);
    }

}
