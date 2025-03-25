package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import static chess.ChessPiece.PieceType.*;
import static ui.EscapeSequences.*;

public class ChessBoardUI {

    private static ChessBoard board = new ChessBoard();
    private static String playerColor;

    private static final int NUM_SQUARES = 8;

    private static String[][] pieceSymbols = new String[8][8];
    private static final Map<ChessPiece.PieceType, String[]> PIECES_KEY = Map.of(
            KING, new String[] {WHITE_KING, BLACK_KING},
            QUEEN, new String[] {WHITE_QUEEN, BLACK_QUEEN},
            BISHOP, new String[] {WHITE_BISHOP, BLACK_BISHOP},
            KNIGHT, new String[] {WHITE_KNIGHT, BLACK_KNIGHT},
            ROOK, new String[] {WHITE_ROOK, BLACK_ROOK},
            PAWN, new String[] {WHITE_PAWN, BLACK_PAWN}
    );


    public ChessBoardUI(ChessGame game, String playerColor) {
        this.board = game.getBoard();
        this.playerColor = playerColor;

        printBoard();
    }

    public static void printBoard() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        setPieceSymbols();

        out.print(ERASE_SCREEN);

        drawHeaders(out);

        drawBoard(out);

        drawHeaders(out);

        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setPieceSymbols() {
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

    private static void drawHeaders(PrintStream out) {

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

    private static void drawHeader(PrintStream out, String headerText) {
        printHeaderText(out, headerText);
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        out.print(RESET_BG_COLOR);
    }

    private static void drawBoard(PrintStream out) {

        String[] rowNumbers = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};
        String firstSquareColor = SET_BG_COLOR_WHITE;

        for (int boardRow = 0; boardRow < NUM_SQUARES; ++boardRow) {

            if (playerColor.equalsIgnoreCase("white")) {
                drawRowOfSquares(out, rowNumbers[8-(boardRow+1)], firstSquareColor, pieceSymbols[boardRow]);
            } else {
                drawRowOfSquares(out, rowNumbers[boardRow], firstSquareColor, pieceSymbols[boardRow]);
            }

            firstSquareColor = switchColor(firstSquareColor);
        }
    }

    private static void drawRowOfSquares(PrintStream out, String rowNumber, String startColor, String[] pieceSymbols) {

        drawHeader(out, rowNumber);

        String squareColor = startColor;

        for (int boardCol = 0; boardCol < NUM_SQUARES; ++boardCol) {

            out.print(squareColor);
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
