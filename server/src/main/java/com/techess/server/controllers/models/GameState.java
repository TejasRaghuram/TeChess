package com.techess.server.controllers.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
    private Piece[] board;
    private char turn;
    private String castling;
    private String enPassant;
    private int halfMoveClock;
    private int fullMoves;

    public GameState(String state) {
        String[] data = state.split(" ");

        board = new Piece[64];
        Map<Character, Piece> pieceMap = Map.ofEntries(
            Map.entry('K', Piece.WHITE_KING),
            Map.entry('k', Piece.BLACK_KING),
            Map.entry('Q', Piece.WHITE_QUEEN),
            Map.entry('q', Piece.BLACK_QUEEN),
            Map.entry('R', Piece.WHITE_ROOK),
            Map.entry('r', Piece.BLACK_ROOK),
            Map.entry('B', Piece.WHITE_BISHOP),
            Map.entry('b', Piece.BLACK_BISHOP),
            Map.entry('N', Piece.WHITE_KNIGHT),
            Map.entry('n', Piece.BLACK_KNIGHT),
            Map.entry('P', Piece.WHITE_PAWN),
            Map.entry('p', Piece.BLACK_PAWN)
        );
        int rank = 7;
        int file = 0;
        for (String row : data[0].split("/")) {
            for (int i = 0; i < row.length(); i++) {
                if (pieceMap.containsKey(row.charAt(i))) {
                    board[getIndex(rank, file)] = pieceMap.get(row.charAt(i));
                    file++;
                } else {
                    for (int j = 0; j < Integer.parseInt(Character.toString(row.charAt(i))); j++) {
                        board[getIndex(rank, file)] = Piece.EMPTY;
                        file++;
                    }
                }
            }
            rank--;
            file = 0;
        }

        turn = data[1].charAt(0);
        castling = data[2];
        enPassant = data[3];
        halfMoveClock = Integer.parseInt(data[4]);
        fullMoves = Integer.parseInt(data[5]);
    }

    public GameState() {
        this(start());
    }

    private static String start() {
        return "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    }

    public void move(String start, String end) {

    }

    public Map<String, List<String>> getMoves() {
        return new HashMap<>();
    }

    public String toString() {
        String result = "";
        
        Map<Piece, Character> pieceMap = Map.ofEntries(
            Map.entry(Piece.WHITE_KING, 'K'),
            Map.entry(Piece.BLACK_KING, 'k'),
            Map.entry(Piece.WHITE_QUEEN, 'Q'),
            Map.entry(Piece.BLACK_QUEEN, 'q'),
            Map.entry(Piece.WHITE_ROOK, 'R'),
            Map.entry(Piece.BLACK_ROOK, 'r'),
            Map.entry(Piece.WHITE_BISHOP, 'B'),
            Map.entry(Piece.BLACK_BISHOP, 'b'),
            Map.entry(Piece.WHITE_KNIGHT, 'N'),
            Map.entry(Piece.BLACK_KNIGHT, 'n'),
            Map.entry(Piece.WHITE_PAWN, 'P'),
            Map.entry(Piece.BLACK_PAWN, 'p')
        );
        for (int rank = 7; rank >= 0; rank--) {
            int blank = 0;
            for (int file = 0; file < 8; file++) {
                if (pieceMap.containsKey(board[getIndex(rank, file)])) {
                    if (blank > 0) {
                        result += Integer.toString(blank);
                        blank = 0;
                    }
                    result += pieceMap.get(board[getIndex(rank, file)]);
                } else {
                    blank++;
                }
            }
            if (blank > 0) {
                result += Integer.toString(blank);
                blank = 0;
            }
            result += "/";
        }
        result = result.substring(0, result.length() - 1) + " ";

        result += turn + " ";
        result += castling + " ";
        result += enPassant + " ";
        result += Integer.toString(halfMoveClock) + " ";
        result += Integer.toString(fullMoves);
        
        return result;
    }

    private int getIndex(int rank, int file) {
        return 8 * rank + file;
    }

    private enum Piece {
        WHITE_KING, BLACK_KING,
        WHITE_QUEEN, BLACK_QUEEN,
        WHITE_ROOK, BLACK_ROOK,
        WHITE_BISHOP, BLACK_BISHOP,
        WHITE_KNIGHT, BLACK_KNIGHT,
        WHITE_PAWN, BLACK_PAWN,
        EMPTY
    }
}
