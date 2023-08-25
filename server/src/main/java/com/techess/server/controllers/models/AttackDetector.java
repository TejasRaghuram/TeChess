package com.techess.server.controllers.models;

import com.techess.server.controllers.models.GameState.Piece;

public class AttackDetector {

    public static boolean inCheck(GameState state) {
        int index = -1;
        for (int i = 0; i < state.board.length; i++) 
            if ((state.turn == 'w' && state.board[i] == Piece.WHITE_KING) 
                || (state.turn == 'b' && state.board[i] == Piece.BLACK_KING)) index = i;
        return attackedByPawn(state, index) 
            || attackedByKnight(state, index) 
            || attackedByKing(state, index) 
            || attackedByBishop(state, index) 
            || attackedByRook(state, index) 
            || attackedByQueen(state, index);
    }

    private static boolean attackedByKing(GameState state, int index) {
        Piece attacker = state.isWhite(index) ? Piece.BLACK_KING : Piece.WHITE_KING;
        int rank = index / 8;
        int file = index % 8;
        for (int i = Math.max(rank - 1, 0); i <= Math.min(rank + 1, 7); i++) {
            for (int j = Math.max(file - 1, 0); j <= Math.min(file + 1, 7); j++) {
                if (i != rank || j != file) {
                    if (state.board[Move.getIndex(i, j)] == attacker) return true;
                }
            }
        }
        return false;
    }

    private static boolean attackedByQueen(GameState state, int index) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
        return attackedByLine(state, index, directions, state.isWhite(index) ? Piece.BLACK_QUEEN : Piece.WHITE_QUEEN);
    }

    private static boolean attackedByRook(GameState state, int index) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        return attackedByLine(state, index, directions, state.isWhite(index) ? Piece.BLACK_ROOK : Piece.WHITE_ROOK);
    }

    private static boolean attackedByBishop(GameState state, int index) {
        int[][] directions = {{1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
        return attackedByLine(state, index, directions, state.isWhite(index) ? Piece.BLACK_BISHOP : Piece.WHITE_BISHOP);
    }

    private static boolean attackedByKnight(GameState state, int index) {
        Piece attacker = state.isWhite(index) ? Piece.BLACK_KNIGHT : Piece.WHITE_KNIGHT;
        int rank = index / 8;
        int file = index % 8;
        int[][] targets = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
        for (int[] target : targets) {
            int r = rank + target[0];
            int f = file + target[1];
            if (0 <= r && r <= 7 && 0 <= f && f <= 7) if (state.board[Move.getIndex(r, f)] == attacker) return true;
        }
        return false;
    }

    private static boolean attackedByPawn(GameState state, int index) {
        Piece attacker = state.isWhite(index) ? Piece.BLACK_PAWN : Piece.WHITE_PAWN;
        int rank = index / 8;
        int file = index % 8;
        int direction = state.isWhite(index) ? 1 : -1;
        if (0 <= rank + direction && rank + direction <= 7) {
            if (file < 7 && state.board[Move.getIndex(rank + direction, file + 1)] == attacker) return true;
            if (file > 0 && state.board[Move.getIndex(rank + direction, file - 1)] == attacker) return true;
        }
        return false;
    }

    private static boolean attackedByLine(GameState state, int index, int[][] directions, Piece attacker) {
        int rank = index / 8;
        int file = index % 8;
        for (int[] direction : directions) {
            int r = rank + direction[0];
            int f = file + direction[1];
            while (0 <= r && r <= 7 && 0 <= f && f <= 7) {
                if (state.board[Move.getIndex(r, f)] == Piece.EMPTY) {
                    r += direction[0];
                    f += direction[1];
                } else {
                    if (state.board[Move.getIndex(r, f)] == attacker) return true;
                    break;
                }
            }
        }
        return false;
    }
    
}
