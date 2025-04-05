package ui;

import chess.ChessGame;
import exception.ServiceException;
import facade.ServerFacade;
import model.GameData;

import java.util.Arrays;
import java.util.HashMap;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class GameUI {
    private ServerFacade serverFacade;
    private ChessGame game;
    private String playerColor;
    private HashMap<String, GameData> createdGames;

    public GameUI(ServerFacade serverFacade, HashMap<String, GameData> createdGames, ChessGame game, String playerColor) {
        this.serverFacade = serverFacade;
        this.createdGames = createdGames;
        this.game = game;
        this.playerColor = playerColor;

        System.out.print(SET_TEXT_COLOR_BLUE + "Game started. Type 'help' to view the menu\n");
        new ChessBoardUI(game, playerColor).printBoard();
    }

    public String executeCommand(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = tokens[0];
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

//        try {
            return switch (cmd) {
                case "help" -> help();
                case "redraw" -> redraw();
                case "leave" -> leave();
//                case "move" -> move();
//                case "resign" -> resign(params);
//                case "moves" -> show(params);
                case "quit" -> "quit";
                case null, default -> "Invalid command. Try one of these: \n" + help();
            };
//        } catch (ServiceException e) {
//            return "Unexpected error" + e.getErrorMessage() + "\n";
//        }
    }

    private String help() {
        return "move <CURRENT POSITION> <FINAL POSITION>: to move a piece\n" +
                "moves <POSITION> : to highlight legal moves for piece at position\n" +
                "redraw : to redisplay current game board\n" +
                "resign : to forfeit current game\n" +
                "leave : to leave current game\n" +
                "quit : to exit chess\n" +
                "help : to display this menu\n";
    }

    private String leave() {
//        serverFacade.logout(authToken);
        return "Game ended";
    }

    private String redraw() {
        new ChessBoardUI(game, playerColor).printBoard();
        return "";
    }


}
