package com.techess.server.controllers.models;

import java.util.HashSet;
import java.util.Set;

public class GameState {

    Piece[] board;
    char turn;
    Set<Character> castling;
    String enPassant;
    int halfMoveClock;
    int fullMoves;

    public GameState() {
        this(FENConverter.START);
    }

    public GameState(String state) {
        board = new Piece[64];
        castling = new HashSet<>();
        FENConverter.parseInto(this, state);
    }

    public GameState(GameState other) {
        this.board = other.board.clone();
        this.turn = other.turn;
        this.castling = new HashSet<>(other.castling);
        this.enPassant = other.enPassant;
        this.halfMoveClock = other.halfMoveClock;
        this.fullMoves = other.fullMoves;
    }

    public char getTurn() {
        return turn;
    }

    public char getStatus() {
        if (halfMoveClock >= 50) return 't';
        else if (MoveGenerator.getMoves(this).isEmpty()) {
            if (AttackDetector.inCheck(this)) return turn == 'w' ? 'b' : 'w';
            else return 't';
        }
        return '-';
    }

    public String toString() {
        return FENConverter.getFEN(this);
    }

    public boolean isWhite(int index) {
        return board[index] == Piece.WHITE_KING
            || board[index] == Piece.WHITE_QUEEN
            || board[index] == Piece.WHITE_ROOK
            || board[index] == Piece.WHITE_BISHOP
            || board[index] == Piece.WHITE_KNIGHT
            || board[index] == Piece.WHITE_PAWN;
    }

    public boolean isBlack(int index) {
        return board[index] == Piece.BLACK_KING
            || board[index] == Piece.BLACK_QUEEN
            || board[index] == Piece.BLACK_ROOK
            || board[index] == Piece.BLACK_BISHOP
            || board[index] == Piece.BLACK_KNIGHT
            || board[index] == Piece.BLACK_PAWN;
    }

    public enum Piece {
        WHITE_KING, BLACK_KING,
        WHITE_QUEEN, BLACK_QUEEN,
        WHITE_ROOK, BLACK_ROOK,
        WHITE_BISHOP, BLACK_BISHOP,
        WHITE_KNIGHT, BLACK_KNIGHT,
        WHITE_PAWN, BLACK_PAWN,
        EMPTY
    }

}
