package com.techess.server.controllers.models;

import java.util.Map;

import com.techess.server.controllers.models.GameState.Piece;

public class FENConverter {

    public static final String START = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private static final Map<Character, GameState.Piece> PIECES = Map.ofEntries(
        Map.entry('K', GameState.Piece.WHITE_KING),
        Map.entry('k', GameState.Piece.BLACK_KING),
        Map.entry('Q', GameState.Piece.WHITE_QUEEN),
        Map.entry('q', GameState.Piece.BLACK_QUEEN),
        Map.entry('R', GameState.Piece.WHITE_ROOK),
        Map.entry('r', GameState.Piece.BLACK_ROOK),
        Map.entry('B', GameState.Piece.WHITE_BISHOP),
        Map.entry('b', GameState.Piece.BLACK_BISHOP),
        Map.entry('N', GameState.Piece.WHITE_KNIGHT),
        Map.entry('n', GameState.Piece.BLACK_KNIGHT),
        Map.entry('P', GameState.Piece.WHITE_PAWN),
        Map.entry('p', GameState.Piece.BLACK_PAWN)
    );

    private static final Map<GameState.Piece, Character> SYMBOLS = Map.ofEntries(
        Map.entry(GameState.Piece.WHITE_KING, 'K'),
        Map.entry(GameState.Piece.BLACK_KING, 'k'),
        Map.entry(GameState.Piece.WHITE_QUEEN, 'Q'),
        Map.entry(GameState.Piece.BLACK_QUEEN, 'q'),
        Map.entry(GameState.Piece.WHITE_ROOK, 'R'),
        Map.entry(GameState.Piece.BLACK_ROOK, 'r'),
        Map.entry(GameState.Piece.WHITE_BISHOP, 'B'),
        Map.entry(GameState.Piece.BLACK_BISHOP, 'b'),
        Map.entry(GameState.Piece.WHITE_KNIGHT, 'N'),
        Map.entry(GameState.Piece.BLACK_KNIGHT, 'n'),
        Map.entry(GameState.Piece.WHITE_PAWN, 'P'),
        Map.entry(GameState.Piece.BLACK_PAWN, 'p')
    );

    public static void parseInto(GameState state, String fen) {
        String[] data = fen.split(" ");
        parseBoard(state, data[0]);
        parseTurn(state, data[1]);
        parseCastling(state, data[2]);
        parseEnPassant(state, data[3]);
        parseHalfMoveClock(state, data[4]);
        parseFullMoves(state, data[5]);
    }

    private static void parseBoard(GameState state, String board) {
        int index = 8 * (8 - 1);
        for (int i = 0; i < board.length(); i++) {
            char c = board.charAt(i);
            Piece piece = PIECES.get(c);
            if (c == '/') index -= 2 * 8;
            else if (piece != null) state.board[index++] = piece;
            else for (int j = 0; j < c - '0'; j++) state.board[index++] = Piece.EMPTY;
        }
    }

    private static void parseTurn(GameState state, String turn) {
        state.turn = turn.charAt(0);
    }

    private static void parseCastling(GameState state, String castling) {
        if (castling.equals("-")) return;
        for (int i = 0; i < castling.length(); i++) state.castling.add(castling.charAt(i));
    }

    private static void parseEnPassant(GameState state, String enPassant) {
        state.enPassant = enPassant;
    }

    private static void parseHalfMoveClock(GameState state, String halfMoveClock) {
        state.halfMoveClock = Integer.parseInt(halfMoveClock);
    }

    private static void parseFullMoves(GameState state, String fullMoves) {
        state.fullMoves = Integer.parseInt(fullMoves);
    }

    public static String getFEN(GameState state) {
        return new StringBuilder(100)
            .append(getFENBoard(state)).append(' ')
            .append(getFENTurn(state)).append(' ')
            .append(getFENCastling(state)).append(' ')
            .append(getFENEnPassant(state)).append(' ')
            .append(getFENHalfMoveClock(state)).append(' ')
            .append(getFENFullMoves(state))
            .toString();
    }

    private static String getFENBoard(GameState state) {
        StringBuilder board = new StringBuilder(8 * 8 + 1);
        int index = 8 * (8 - 1);
        for (int i = 0; i < 8; i++) {
            int empty = 0;
            for (int j = 0; j < 8; j++) {
                Character symbol = SYMBOLS.get(state.board[index]);
                if (symbol != null) {
                    if (empty > 0) {
                        board.append(empty);
                        empty = 0;
                    }
                    board.append(symbol);
                } else empty++;
                index++;
            }
            if (empty > 0) board.append(empty);
            if (index > 8) board.append('/');
            index -= 2 * 8;
        }
        return board.toString();
    }

    private static String getFENTurn(GameState state) {
        return Character.toString(state.turn);
    }

    private static String getFENCastling(GameState state) {
        StringBuilder sb = new StringBuilder(4);
        for (char c : state.castling) sb.append(c);
        return sb.toString();
    }

    private static String getFENEnPassant(GameState state) {
        return state.enPassant;
    }

    private static String getFENHalfMoveClock(GameState state) {
        return Integer.toString(state.halfMoveClock);
    }

    private static String getFENFullMoves(GameState state) {
        return Integer.toString(state.fullMoves);
    }
    
}
