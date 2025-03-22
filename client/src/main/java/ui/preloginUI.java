package ui;

import chess.ChessPiece;
import model.AuthData;
import serverFacade.ServerFacade;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

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
//        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
//        System.out.print(SET_TEXT_COLOR_BLUE + help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = executeCommand(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();

//        executeCommand(out, args);
    }

    private String executeCommand(String input) {
        var tokens = input.toLowerCase().split(" ");
//        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var cmd = tokens[0];
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (Objects.equals(cmd, "help")) {
            return help();
        } else if (Objects.equals(cmd, "register")) {
            return register(params);
        } else if (Objects.equals(cmd, "quit")) {
            return "quit";
        } else if (Objects.equals(cmd, "login")) {
            return login(params);
        } else {
            return "not implemented";
        }
//        printPrompt();
    }

    private String help() {
//        out.print(SET_TEXT_COLOR_BLUE);
//        out.print("register <USERNAME> <PASSWORD> <EMAIL> : to create an account\n" +
//                "login <USERNAME> <PASSWORD> : to login to play chess\n" +
//                "quit : exit the game\n" +
//                "help : to view this menu");
        String text =
                "register <USERNAME> <PASSWORD> <EMAIL> : to create an account\n" +
                "login <USERNAME> <PASSWORD> : to login to your account\n" +
                "quit : to exit chess\n" +
                "help : to view this menu\n";

        return text;
    }

    private String register(String[] params) {
//        AuthData registrationData = ServerFacade.register(params[0], params[1], params[2]);
        return "register not implemented";
    }

    private String login(String[] params) {
        return "login not implemented";
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + "[LOGGED OUT] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
