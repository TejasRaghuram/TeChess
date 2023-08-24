package com.techess.server.controllers.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameState {
    private static final String START = "rnbqkbnr-pppppppp-8-8-8-8-PPPPPPPP-RNBQKBNR w KQkq - 0 1";

    private Piece[] board;
    private char turn;
    private Set<Character> castling;
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
        for (String row : data[0].split("-")) {
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
        castling = new HashSet<>();
        for (int i = 0; i < data[2].length(); i++) castling.add(data[2].charAt(i));
        enPassant = data[3];
        halfMoveClock = Integer.parseInt(data[4]);
        fullMoves = Integer.parseInt(data[5]);
    }

    public GameState() {
        this(START);
    }

    public GameState(GameState other) {
        this.board = other.board.clone();
        this.turn = other.turn;
        this.castling = new HashSet<>(other.castling);
        this.enPassant = other.enPassant;
        this.halfMoveClock = other.halfMoveClock;
        this.fullMoves = other.fullMoves;
    }

    public void move(String start, String end) {
        int startRank = Integer.parseInt(start.substring(1)) - 1;
        int startFile = (int) (start.charAt(0) - 'a');
        int endRank = Integer.parseInt(end.substring(1)) - 1;
        int endFile = (int) (end.charAt(0) - 'a');
        move(getIndex(startRank, startFile), getIndex(endRank, endFile));
    }

    private void move(int start, int end) {
        if (board[start] == Piece.WHITE_PAWN || board[start] == Piece.BLACK_PAWN || board[end] != Piece.EMPTY) halfMoveClock = 0;
        else halfMoveClock++;

        board[end] = board[start];
        board[start] = Piece.EMPTY;

        turn = turn == 'w' ? 'b' : 'w';

        if (turn == 'w') fullMoves++;
    }

    public Map<String, List<String>> getMoves() {
        Map<Integer, List<Integer>> moves = getLegalMoves();
        Map<String, List<String>> result = new HashMap<>();
        for (Integer start : moves.keySet()) {
            String s = getSquare(start);
            result.put(s, new ArrayList<>());
            for (Integer end : moves.get(start)) {
                result.get(s).add(getSquare(end));
            }
        }
        return result;
    }

    private Map<Integer, List<Integer>> getLegalMoves() {
        Map<Integer, List<Integer>> moves = getPseudoMoves();
        for (Integer start : moves.keySet()) {
            Iterator<Integer> iterator = moves.get(start).iterator();
            while (iterator.hasNext()) {
                Integer end = iterator.next();
                Piece startPiece = board[start];
                Piece endPiece = board[end];
                board[end] = startPiece;
                board[start] = Piece.EMPTY;
                if (inCheck()) iterator.remove();
                board[start] = startPiece;
                board[end] = endPiece;
            }
        }
        moves.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        return moves;
    }

    private boolean inCheck() {
        return attackedByKing() 
            || attackedByQueen() 
            || attackedByRook() 
            || attackedByBishop() 
            || attackedByKnight() 
            || attackedByPawn();
    }

    private boolean attackedByKing() {
        int index = getKing();
        Piece attacker = isWhite(index) ? Piece.BLACK_KING : Piece.WHITE_KING;
        int rank = index / 8;
        int file = index % 8;
        for (int i = Math.max(rank - 1, 0); i <= Math.min(rank + 1, 7); i++) {
            for (int j = Math.max(file - 1, 0); j <= Math.min(file + 1, 7); j++) {
                if (i != rank || j != file) {
                    if (board[getIndex(i, j)] == attacker) return true;
                }
            }
        }
        return false;
    }

    private boolean attackedByQueen() {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
        return attackedByLine(directions, isWhite(getKing()) ? Piece.BLACK_QUEEN : Piece.WHITE_QUEEN);
    }

    private boolean attackedByRook() {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        return attackedByLine(directions, isWhite(getKing()) ? Piece.BLACK_ROOK : Piece.WHITE_ROOK);
    }

    private boolean attackedByBishop() {
        int[][] directions = {{1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
        return attackedByLine(directions, isWhite(getKing()) ? Piece.BLACK_BISHOP : Piece.WHITE_BISHOP);
    }

    private boolean attackedByKnight() {
        int index = getKing();
        Piece attacker = isWhite(index) ? Piece.BLACK_KNIGHT : Piece.WHITE_KNIGHT;
        int rank = index / 8;
        int file = index % 8;
        int[][] targets = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
        for (int[] target : targets) {
            int r = rank + target[0];
            int f = file + target[1];
            if (0 <= r && r <= 7 && 0 <= f && f <= 7) if (board[getIndex(r, f)] == attacker) return true;
        }
        return false;
    }

    private boolean attackedByPawn() {
        int index = getKing();
        Piece attacker = isWhite(index) ? Piece.BLACK_PAWN : Piece.WHITE_PAWN;
        int rank = index / 8;
        int file = index % 8;
        int direction = isWhite(index) ? 1 : -1;
        if (0 <= rank + direction && rank + direction <= 7) {
            if (file < 7 && board[getIndex(rank + direction, file + 1)] == attacker) return true;
            if (file > 0 && board[getIndex(rank + direction, file - 1)] == attacker) return true;
        }
        return false;
    }

    private boolean attackedByLine(int[][] directions, Piece attacker) {
        int index = getKing();
        int rank = index / 8;
        int file = index % 8;
        for (int[] direction : directions) {
            int r = rank + direction[0];
            int f = file + direction[1];
            while (0 <= r && r <= 7 && 0 <= f && f <= 7) {
                if (board[getIndex(r, f)] == Piece.EMPTY) {
                    r += direction[0];
                    f += direction[1];
                } else {
                    if (board[getIndex(r, f)] == attacker) return true;
                    break;
                }
            }
        }
        return false;
    }

    private int getKing() {
        for (int i = 0; i < board.length; i++) {
            if ((turn == 'w' && board[i] == Piece.WHITE_KING) || (turn == 'b' && board[i] == Piece.BLACK_KING)) return i;
        }
        return -1;
    }

    private Map<Integer, List<Integer>> getPseudoMoves() {
        Map<Integer, List<Integer>> moves = new HashMap<>();
        for (int i = 0; i < board.length; i++) {
            if ((turn == 'w' && isWhite(i)) || (turn == 'b' && isBlack(i))) moves.put(i, getPseudoMoves(i));
        }
        return moves;
    }

    private List<Integer> getPseudoMoves(int index) {
        switch (board[index]) {
            case WHITE_KING:
            case BLACK_KING:
                return getKingMoves(index);
            case WHITE_QUEEN:
            case BLACK_QUEEN:
                return getQueenMoves(index);
            case WHITE_ROOK:
            case BLACK_ROOK:
                return getRookMoves(index);
            case WHITE_BISHOP:
            case BLACK_BISHOP:
                return getBishopMoves(index);
            case WHITE_KNIGHT:
            case BLACK_KNIGHT:
                return getKnightMoves(index);
            case WHITE_PAWN:
            case BLACK_PAWN:
                return getPawnMoves(index);
            default:
                return new ArrayList<>();
        }
    }

    private List<Integer> getKingMoves(int index) {
        List<Integer> moves = new ArrayList<>();
        int rank = index / 8;
        int file = index % 8;
        for (int i = Math.max(rank - 1, 0); i <= Math.min(rank + 1, 7); i++) {
            for (int j = Math.max(file - 1, 0); j <= Math.min(file + 1, 7); j++) {
                if (i != rank || j != file) {
                    int neighbor = getIndex(i, j);
                    if (board[neighbor] == Piece.EMPTY || (isWhite(index) && isBlack(neighbor)) || (isBlack(index) && isWhite(neighbor))) {
                        moves.add(neighbor);
                    }
                }
            }
        }
        return moves;
    }

    private List<Integer> getQueenMoves(int index) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
        return getLineMoves(index, directions);
    }

    private List<Integer> getRookMoves(int index) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        return getLineMoves(index, directions);
    }

    private List<Integer> getBishopMoves(int index) {
        int[][] directions = {{1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
        return getLineMoves(index, directions);
    }

    private List<Integer> getKnightMoves(int index) {
        List<Integer> moves = new ArrayList<>();
        int rank = index / 8;
        int file = index % 8;
        int[][] targets = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
        for (int[] target : targets) {
            int r = rank + target[0];
            int f = file + target[1];
            if (0 <= r && r <= 7 && 0 <= f && f <= 7) {
                if ((isWhite(index) && !isWhite(getIndex(r, f))) || (isBlack(index) && !isBlack(getIndex(r, f)))) moves.add(getIndex(r, f));
            }
        }
        return moves;
    }

    private List<Integer> getPawnMoves(int index) {
        List<Integer> moves = new ArrayList<>();
        int rank = index / 8;
        int file = index % 8;
        int direction = isWhite(index) ? 1 : -1;
        int start = isWhite(index) ? 1 : 6;
        if (0 <= rank + direction && rank + direction <= 7) { 
            if (board[getIndex(rank + direction, file)] == Piece.EMPTY) {
                moves.add(getIndex(rank + direction, file));
                if (rank == start && board[getIndex(rank + 2 * direction, file)] == Piece.EMPTY) moves.add(getIndex(rank + 2 * direction, file));
            }
            if (file < 7 && ((isWhite(index) && isBlack(getIndex(rank + direction, file + 1))) || (isBlack(index) && isWhite(getIndex(rank + direction, file + 1))))) {
                moves.add(getIndex(rank + direction, file + 1));
            }
            if (file > 0 && ((isWhite(index) && isBlack(getIndex(rank + direction, file - 1))) || (isBlack(index) && isWhite(getIndex(rank + direction, file - 1))))) {
                moves.add(getIndex(rank + direction, file - 1));
            }
        }
        return moves;
    }

    private List<Integer> getLineMoves(int index, int[][] directions) {
        List<Integer> moves = new ArrayList<>();
        int rank = index / 8;
        int file = index % 8;
        for (int[] direction : directions) {
            int r = rank + direction[0];
            int f = file + direction[1];
            while (0 <= r && r <= 7 && 0 <= f && f <= 7) {
                if (board[getIndex(r, f)] == Piece.EMPTY) {
                    moves.add(getIndex(r, f));
                    r += direction[0];
                    f += direction[1];
                } else {
                    if ((isWhite(index) && isBlack(getIndex(r, f))) || (isBlack(index) && isWhite(getIndex(r, f)))) {
                        moves.add(getIndex(r, f));
                    }
                    break;
                }
            }
        }
        return moves;
    }

    public String gameStatus() {
        if (halfMoveClock >= 50) return "t";
        else if (getMoves().isEmpty()) {
            if (inCheck()) return turn == 'w' ? "b" : "w";
            else return "t";
        }
        return "";
    }

    public String simulateGame() {
        GameState simulation = new GameState(this);
        while (simulation.gameStatus().equals("")) {
            Map<String, List<String>> moves = simulation.getMoves();
            int i = (int) (Math.random() * moves.values().stream().mapToInt(List::size).sum());
            for (String start : moves.keySet()) {
                if (i >= moves.get(start).size()) i -= moves.get(start).size();
                else {
                    simulation.move(start, moves.get(start).get(i));
                    break;
                }
            }
        }
        return simulation.gameStatus();
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
            result += "-";
        }
        result = result.substring(0, result.length() - 1) + " ";

        result += turn + " ";
        for (Character c : castling) result += c;
        result += " ";
        result += enPassant + " ";
        result += Integer.toString(halfMoveClock) + " ";
        result += Integer.toString(fullMoves);
        
        return result;
    }

    private int getIndex(int rank, int file) {
        return 8 * rank + file;
    }

    private String getSquare(int index) {
        int rank = index / 8;
        int file = index % 8;
        return getSquare(rank, file);
    }

    private String getSquare(int rank, int file) {
        return Character.toString('a' + file) + Integer.toString(rank + 1);
    }

    private boolean isWhite(int index) {
        return board[index] == Piece.WHITE_KING
            || board[index] == Piece.WHITE_QUEEN
            || board[index] == Piece.WHITE_ROOK
            || board[index] == Piece.WHITE_BISHOP
            || board[index] == Piece.WHITE_KNIGHT
            || board[index] == Piece.WHITE_PAWN;
    }

    private boolean isBlack(int index) {
        return board[index] == Piece.BLACK_KING
            || board[index] == Piece.BLACK_QUEEN
            || board[index] == Piece.BLACK_ROOK
            || board[index] == Piece.BLACK_BISHOP
            || board[index] == Piece.BLACK_KNIGHT
            || board[index] == Piece.BLACK_PAWN;
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
