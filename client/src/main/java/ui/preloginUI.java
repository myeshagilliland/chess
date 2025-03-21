package ui;

import chess.ChessPiece;
import model.AuthData;
import serverFacade.ServerFacade;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import static chess.ChessPiece.PieceType.*;
import static chess.ChessPiece.PieceType.PAWN;
import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_PAWN;

public class preloginUI {

//    private static final Map<ChessPiece.PieceType, String[]> piecesKey = Map.of(
//            KING, new String[] {WHITE_KING, BLACK_KING},
//            QUEEN, new String[] {WHITE_QUEEN, BLACK_QUEEN},
//            BISHOP, new String[] {WHITE_BISHOP, BLACK_BISHOP},
//            KNIGHT, new String[] {WHITE_KNIGHT, BLACK_KNIGHT},
//            ROOK, new String[] {WHITE_ROOK, BLACK_ROOK},
//            PAWN, new String[] {WHITE_PAWN, BLACK_PAWN}
//    );

    public preloginUI(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        executeCommand(out, args);
    }

    private void executeCommand(PrintStream out, String[] args) {
        if (Objects.equals(args[0], "help")) {
            help(out);
        } else if (Objects.equals(args[0], "register")) {
            register(out, args);
        } else {
            out.print("not implemented");
        }
    }

    private void help(PrintStream out) {
        out.print(SET_TEXT_COLOR_BLUE);
        out.print("register <USERNAME> <PASSWORD> <EMAIL> : to create an account\n" +
                "login <USERNAME> <PASSWORD> : to login to play chess\n" +
                "quit : exit the game\n" +
                "help : to view this menu");
    }

    private void register(PrintStream out, String[] args) {
//        AuthData registrationData = ServerFacade.register(args[1], args[2], args[3]);
    }

}
