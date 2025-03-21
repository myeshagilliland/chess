package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static chess.ChessPiece.PieceType.*;
import static ui.EscapeSequences.*;

public class chessBoardUI {

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 0;

    // Padded characters.
//    private static final String EMPTY = "   "
//    private static final String X = " X ";
//    private static final String O = " O ";
//    private static Random rand = new Random();


    private static ChessBoard board = new ChessBoard();
    private static String playerColor;
    private static String[][] pieceSymbols = new String[8][8];
    private static final Map<ChessPiece.PieceType, String[]> piecesKey = Map.of(
            KING, new String[] {WHITE_KING, BLACK_KING},
            QUEEN, new String[] {WHITE_QUEEN, BLACK_QUEEN},
            BISHOP, new String[] {WHITE_BISHOP, BLACK_BISHOP},
            KNIGHT, new String[] {WHITE_KNIGHT, BLACK_KNIGHT},
            ROOK, new String[] {WHITE_ROOK, BLACK_ROOK},
            PAWN, new String[] {WHITE_PAWN, BLACK_PAWN}
    );

    private static void setPieceSymbols() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                ChessPosition pos;
                if (Objects.equals(playerColor, "WHITE")) {
                    pos = new ChessPosition(8-(row), (col+1));
                } else {
                    pos = new ChessPosition(row+1, 8-(col));
                }

                ChessPiece piece = board.getPiece(pos);
                String pieceSymbol;

                if (piece == null) {
                    pieceSymbol = EMPTY;
                } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    pieceSymbol = piecesKey.get(piece.getPieceType())[0];
                } else {
                    pieceSymbol = piecesKey.get(piece.getPieceType())[1];
                }
                pieceSymbols[row][col] = pieceSymbol;
            }
        }
    }


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        playerColor = "WHITE";
//        playerColor = "BLACK";

        board.resetBoard();
        setPieceSymbols();

        out.print(ERASE_SCREEN);

        drawHeaders(out);

        drawTicTacToeBoard(out);

        drawHeaders(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out) {

        setBlack(out);

        String[] headers = { EMPTY, " a ", " b ", " c ", " d ", " e ", " f " , " g ", " h ", EMPTY};
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES + 2; ++boardCol) {

            if (playerColor == "WHITE") {
                drawHeader(out, headers[boardCol]);
            } else {
                drawHeader(out, headers[10-(boardCol+1)]);
            }


//            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
//                out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
//            }
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

        out.print(EMPTY.repeat(prefixLength));
        printHeaderText(out, headerText);
        out.print(EMPTY.repeat(suffixLength));
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setBlack(out);
    }

    private static void drawTicTacToeBoard(PrintStream out) {

        String[] rowNumbers = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};
        String firstSquareColor = SET_BG_COLOR_WHITE;

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            if (playerColor == "WHITE") {
                drawRowOfSquares(out, rowNumbers[8-(boardRow+1)], firstSquareColor, pieceSymbols[boardRow]);
            } else {
                drawRowOfSquares(out, rowNumbers[boardRow], firstSquareColor, pieceSymbols[boardRow]);
            }


//            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
//                // Draw horizontal row separator.
//                drawHorizontalLine(out);
//                setBlack(out);
//            }
            firstSquareColor = switchColor(firstSquareColor);
        }
    }

    private static void drawRowOfSquares(PrintStream out, String rowNumber, String startColor, String[] pieceSymbols) {

        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {

            drawHeader(out, rowNumber);

            String squareColor = startColor;
            String teamColor = SET_TEXT_COLOR_BLACK;

            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {

                if (Objects.equals(squareColor, SET_BG_COLOR_WHITE)) {
                    setWhite(out);
                } else {
                    setBlack(out);
                }


                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

                    out.print(EMPTY.repeat(prefixLength));
//                    rand.nextBoolean() ? X : O
                    printPlayer(out, pieceSymbols[boardCol], squareColor, teamColor);
                    out.print(EMPTY.repeat(suffixLength));
                }
                else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                }

//                if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
//                    // Draw vertical column separator.
//                    setRed(out);
//                    out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
//                }

//                setBlack(out);

                squareColor = switchColor(squareColor);
            }

            drawHeader(out, rowNumber);

            out.println();
        }
    }

//    private static void drawHorizontalLine(PrintStream out) {
//
//        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_PADDED_CHARS +
//                                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_PADDED_CHARS;
//
//        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
//            setRed(out);
//            out.print(EMPTY.repeat(boardSizeInSpaces));
//
//            setBlack(out);
//            out.println();
//        }
//    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printPlayer(PrintStream out, String player, String squareColor, String teamColor) {
        out.print(squareColor);
        out.print(teamColor);

        out.print(player);

        setWhite(out);
    }

    private static String switchColor(String color) {
        if (Objects.equals(color, SET_BG_COLOR_WHITE)) {
            return SET_BG_COLOR_BLUE;
        } else {
            return  SET_BG_COLOR_WHITE;
        }
    }


}
