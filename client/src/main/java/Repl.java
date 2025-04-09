import chess.ChessGame;
import ui.ChessBoardUI;
import ui.GameUI;
import ui.PostLoginUI;
import ui.PreLoginUI;
import websocket.NotificationHandler;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Objects;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final PreLoginUI preLoginUI;
    private PostLoginUI postLoginUI;
    private GameUI gameUI;
    private ChessGame game;

    public Repl(int port) {
        preLoginUI = new PreLoginUI(port, this);
    }

    public void run() {
        System.out.println("Welcome to Chess! Type 'help' to begin");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        String ui = "preLogin";
        while (!result.equals("quit")) {
            printPrompt(ui);
            String line = scanner.nextLine();

            try {

                if (ui.equals("preLogin")) {
                    result = preLoginUI.executeCommand(line);
                } else if (ui.equals("game")) {
                    result = gameUI.executeCommand(line);
                } else {
                    result = postLoginUI.executeCommand(line);
                }

                if (Objects.equals(result, "Logged in")) {
                    postLoginUI = preLoginUI.getPostLoginUI();
                    ui = "postLogin";
                    result = "";
                } else if (Objects.equals(result, "Logged out")) {
                    postLoginUI = null;
                    ui = "preLogin";
                    result = "Logged out. Type 'help' to view the menu\n";
                } else if (Objects.equals(result, "Game started")) {
                    gameUI = postLoginUI.getGameUI();
                    ui = "game";
                    result = "";
                } else if (Objects.equals(result, "Game ended")) {
                    gameUI = null;
                    ui = "postLogin";
                    result = "You have left the game. Type 'help' to view the menu\n";
                }

                if (Objects.equals(result, "quit")) {
                    System.out.print(SET_TEXT_COLOR_BLUE + "Thank you for playing Chess! Goodbye");
                } else {
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                }

            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
//            printPrompt(ui);
        }
        System.out.println();
    }

    public void sendNotification(NotificationMessage notification) {
//        System.out.println();
        System.out.println(SET_TEXT_COLOR_YELLOW + notification.getMessage());
        printPrompt("game");
//        System.out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + "[GAME PLAY] >>> " + SET_TEXT_COLOR_GREEN);
    }

    public void sendError(ErrorMessage error) {
//        System.out.println();
        System.out.println(SET_TEXT_COLOR_RED + error.getMessage());
        printPrompt("game");
//        System.out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + "[GAME PLAY] >>> " + SET_TEXT_COLOR_GREEN);
    }

    public void sendLoadGame(LoadGameMessage loadGameMessage, String playerColor) {
//        game = loadGameMessage.getGame();
        gameUI.updateGame(loadGameMessage.getGame());
        System.out.println();
        new ChessBoardUI(loadGameMessage.getGame(), playerColor).printBoard(); //FIX THIS!!!
        printPrompt("game");
//        System.out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + "[GAME PLAY] >>> " + SET_TEXT_COLOR_GREEN);
    }

//    public ChessGame getGame() {return game;};

    private void printPrompt(String ui) {
        String message;
        if (Objects.equals(ui, "game")) {
            message = "[GAME PLAY] >>> ";
        } else if (Objects.equals(ui, "postLogin")) {
            message = "[LOGGED IN] >>> ";
        } else {
            message = "[LOGGED OUT] >>> ";
        }
        System.out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + message + SET_TEXT_COLOR_GREEN);
    }
}
