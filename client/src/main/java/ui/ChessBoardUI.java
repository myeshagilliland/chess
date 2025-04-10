package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static chess.ChessPiece.PieceType.*;
import static ui.EscapeSequences.*;

public class ChessBoardUI {

    private ChessBoard board = new ChessBoard();
    private final ChessGame game;
    private final String playerColor;
    private static final int NUM_SQUARES = 8;
    private final String[][] pieceSymbols = new String[8][8];
    private static final Map<ChessPiece.PieceType, String[]> PIECES_KEY = Map.of(
            KING, new String[] {WHITE_KING, BLACK_KING},
            QUEEN, new String[] {WHITE_QUEEN, BLACK_QUEEN},
            BISHOP, new String[] {WHITE_BISHOP, BLACK_BISHOP},
            KNIGHT, new String[] {WHITE_KNIGHT, BLACK_KNIGHT},
            ROOK, new String[] {WHITE_ROOK, BLACK_ROOK},
            PAWN, new String[] {WHITE_PAWN, BLACK_PAWN}
    );


    public ChessBoardUI(ChessGame game, String playerColor) {
        this.game = game;
        this.board = game.getBoard();
        this.playerColor = playerColor;
    }

    public void printBoard() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        setPieceSymbols();

        out.print(ERASE_SCREEN);

        drawHeaders(out);

        drawBoard(out, null);

        drawHeaders(out);

        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    public void highlightMoves(ChessPosition position) {
//        Collection<ChessMove> moves = board.getPiece(position).pieceMoves(board, position);
        Collection<ChessMove> moves = game.validMoves(position);
        HashMap<Integer, ArrayList<Integer>> possiblePositions = new HashMap<>();
        for (ChessMove move : moves) {
            ChessPosition pos = move.getEndPosition();
            possiblePositions.computeIfAbsent(pos.getRow(), k -> new ArrayList<>()).add(pos.getColumn());
        }

        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        setPieceSymbols();

        out.print(ERASE_SCREEN);

        drawHeaders(out);

        drawBoard(out, possiblePositions);

        drawHeaders(out);

        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_WHITE);

    }

    private void setPieceSymbols() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                ChessPosition pos;
                if (playerColor.equalsIgnoreCase("white")) {
                    pos = new ChessPosition(8-(row), (col+1));
                } else {
                    pos = new ChessPosition(row+1, 8-(col));
                }

                ChessPiece piece = board.getPiece(pos);
                String pieceSymbol;

                if (piece == null) {
                    pieceSymbol = EMPTY;
                } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    pieceSymbol = PIECES_KEY.get(piece.getPieceType())[0];
                } else {
                    pieceSymbol = PIECES_KEY.get(piece.getPieceType())[1];
                }
                pieceSymbols[row][col] = pieceSymbol;
            }
        }
    }

    private void drawHeaders(PrintStream out) {

        String[] headers = { EMPTY, " a ", " b ", " c ", " d ", " e ", " f " , " g ", " h ", EMPTY};
        for (int boardCol = 0; boardCol < NUM_SQUARES + 2; ++boardCol) {

            if (playerColor.equalsIgnoreCase("white")) {
                drawHeader(out, headers[boardCol]);
            } else {
                drawHeader(out, headers[10-(boardCol+1)]);
            }
        }

        out.println();
    }

    private void drawHeader(PrintStream out, String headerText) {
        printHeaderText(out, headerText);
    }

    private void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        out.print(RESET_BG_COLOR);
    }

    private void drawBoard(PrintStream out, HashMap<Integer, ArrayList<Integer>> possiblePositions) {

        String[] rowNumbers = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};
        String firstSquareColor = SET_BG_COLOR_WHITE;

        for (int boardRow = 0; boardRow < NUM_SQUARES; ++boardRow) {

            ArrayList<Integer> positions = null;
            if (possiblePositions != null && playerColor.equalsIgnoreCase("white")) {
                positions = possiblePositions.get(8-(boardRow));
            } else if (possiblePositions != null && playerColor.equalsIgnoreCase("black")) {
                ArrayList<Integer> unfixedPositions = possiblePositions.get(boardRow+1);
                if (unfixedPositions != null) {
                    positions = new ArrayList<>();
                    for (Integer pos : unfixedPositions) {
                        positions.add(8-(pos-1));
                    }
                }
            }

            if (playerColor.equalsIgnoreCase("white")) {
                drawRowOfSquares(out, rowNumbers[8-(boardRow+1)], firstSquareColor, pieceSymbols[boardRow], positions);
            } else {
                drawRowOfSquares(out, rowNumbers[boardRow], firstSquareColor, pieceSymbols[boardRow], positions);
            }

            firstSquareColor = switchColor(firstSquareColor);
        }
    }

    private void drawRowOfSquares(PrintStream out, String rowNumber, String startColor,
                                         String[] pieceSymbols, ArrayList<Integer> positions) {

        drawHeader(out, rowNumber);

        String squareColor = startColor;

        for (int boardCol = 0; boardCol < NUM_SQUARES; ++boardCol) {

            if (positions != null && positions.contains(boardCol+1)) {
                if (squareColor.equals(SET_BG_COLOR_WHITE)) {
                    out.print(SET_BG_COLOR_GREEN);
                } else {
                    out.print(SET_BG_COLOR_DARK_GREEN);
                }
            } else {
                out.print(squareColor);
            }

            out.print(pieceSymbols[boardCol]);

            squareColor = switchColor(squareColor);
        }

        drawHeader(out, rowNumber);

        out.println();
    }

    private static String switchColor(String color) {
        if (Objects.equals(color, SET_BG_COLOR_WHITE)) {
            return SET_BG_COLOR_BLUE;
        } else {
            return  SET_BG_COLOR_WHITE;
        }
    }


}
