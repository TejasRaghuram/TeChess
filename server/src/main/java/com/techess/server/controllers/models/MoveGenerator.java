package com.techess.server.controllers.models;

import java.util.ArrayList;
import java.util.List;

import com.techess.server.controllers.models.GameState.Piece;

public class MoveGenerator {

    private static int[][] KING_TARGETS = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    private static int[][] QUEEN_DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
    private static int[][] ROOK_DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private static int[][] BISHOP_DIRECTIONS = {{1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
    private static int[][] KNIGHT_TARGETS = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
    
    public static List<Move> getMoves(GameState state) {
        List<Move> moves = new ArrayList<>();
        for (Move move : getPseudoMoves(state)) {
            Piece captured = tryMove(state, move);
            if (!AttackDetector.inCheck(state)) moves.add(move);
            restoreBoard(state, move, captured);
        }
        return moves;
    }

    private static List<Move> getPseudoMoves(GameState state) {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < state.board.length; i++) {
            if (state.turn == 'w' && !state.isWhite(i)) continue;
            if (state.turn == 'b' && !state.isBlack(i)) continue;
            addPieceMoves(state, i, moves);
        }
        return moves;
    }

    private static void addPieceMoves(GameState state, int index, List<Move> moves) {
        switch (state.board[index]) {
            case WHITE_KING, BLACK_KING -> addKingMoves(state, index, moves);
            case WHITE_QUEEN, BLACK_QUEEN -> addQueenMoves(state, index, moves);
            case WHITE_ROOK, BLACK_ROOK -> addRookMoves(state, index, moves);
            case WHITE_BISHOP, BLACK_BISHOP -> addBishopMoves(state, index, moves);
            case WHITE_KNIGHT, BLACK_KNIGHT -> addKnightMoves(state, index, moves);
            case WHITE_PAWN, BLACK_PAWN -> addPawnMoves(state, index, moves);
            default -> {}
        }
    }

    private static void addKingMoves(GameState state, int index, List<Move> moves) {
        addTargetMoves(state, index, KING_TARGETS, moves);
    }

    private static void addQueenMoves(GameState state, int index, List<Move> moves) {
        addLineMoves(state, index, QUEEN_DIRECTIONS, moves);
    }

    private static void addRookMoves(GameState state, int index, List<Move> moves) {
        addLineMoves(state, index, ROOK_DIRECTIONS, moves);
    }

    private static void addBishopMoves(GameState state, int index, List<Move> moves) {
        addLineMoves(state, index, BISHOP_DIRECTIONS, moves);
    }

    private static void addKnightMoves(GameState state, int index, List<Move> moves) {
        addTargetMoves(state, index, KNIGHT_TARGETS, moves);
    }

    private static void addPawnMoves(GameState state, int index, List<Move> moves) {
        int rank = index / 8;
        int file = index % 8;
        int direction = state.isWhite(index) ? 1 : -1;
        int start = state.isWhite(index) ? 1 : 6;
        if (0 <= rank + direction && rank + direction <= 7) { 
            if (state.board[Move.getIndex(rank + direction, file)] == Piece.EMPTY) {
                moves.add(new Move(index, Move.getIndex(rank + direction, file)));
                if (rank == start && state.board[Move.getIndex(rank + 2 * direction, file)] == Piece.EMPTY) 
                    moves.add(new Move(index, Move.getIndex(rank + 2 * direction, file)));
            }
            if (file < 7 
                && ((state.isWhite(index) && state.isBlack(Move.getIndex(rank + direction, file + 1))) 
                || (state.isBlack(index) && state.isWhite(Move.getIndex(rank + direction, file + 1)))))
                moves.add(new Move(index, Move.getIndex(rank + direction, file + 1)));
            if (file > 0 
                && ((state.isWhite(index) && state.isBlack(Move.getIndex(rank + direction, file - 1))) 
                || (state.isBlack(index) && state.isWhite(Move.getIndex(rank + direction, file - 1)))))
                moves.add(new Move(index, Move.getIndex(rank + direction, file - 1)));
        }
    }

    private static void addLineMoves(GameState state, int index, int[][] directions, List<Move> moves) {
        int rank = index / 8;
        int file = index % 8;
        for (int[] direction : directions) {
            int r = rank + direction[0];
            int f = file + direction[1];
            while (0 <= r && r <= 7 && 0 <= f && f <= 7) {
                if (state.board[Move.getIndex(r, f)] == Piece.EMPTY) {
                    moves.add(new Move(index, Move.getIndex(r, f)));
                    r += direction[0];
                    f += direction[1];
                } else {
                    if ((state.isWhite(index) && state.isBlack(Move.getIndex(r, f))) 
                        || (state.isBlack(index) && state.isWhite(Move.getIndex(r, f))))
                        moves.add(new Move(index, Move.getIndex(r, f)));
                    break;
                }
            }
        }
    }

    private static void addTargetMoves(GameState state, int index, int[][] targets, List<Move> moves) {
        int rank = index / 8;
        int file = index % 8;
        for (int[] target : targets) {
            int r = rank + target[0];
            int f = file + target[1];
            if (0 <= r && r <= 7 && 0 <= f && f <= 7) {
                if ((state.isWhite(index) && !state.isWhite(Move.getIndex(r, f))) 
                    || (state.isBlack(index) && !state.isBlack(Move.getIndex(r, f)))) moves.add(new Move(index, Move.getIndex(r, f)));
            }
        }
    }

    private static Piece tryMove(GameState state, Move move) {
        Piece moved = state.board[move.from];
        Piece captured = state.board[move.to];

        state.board[move.to] = moved;
        state.board[move.from] = Piece.EMPTY;

        return captured;
    }

    private static void restoreBoard(GameState state, Move move, Piece captured) {
        Piece moved = state.board[move.to];

        state.board[move.from] = moved;
        state.board[move.to] = captured;
    }

}
