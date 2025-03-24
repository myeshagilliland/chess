package ui;

import chess.ChessBoard;
import chess.ChessGame;
import exception.ServiceException;
import model.GameData;
import requestresult.CreateGameResult;
import requestresult.JoinGameResult;
import requestresult.ListGamesResult;
import serverFacade.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class postloginUI {

    private ServerFacade serverFacade;
    private String authToken;
    private HashMap<String, GameData> gameNumbers = new HashMap<>();

    public postloginUI(ServerFacade serverFacade, String authToken) {
        this.serverFacade = serverFacade;
        this.authToken = authToken;

//        printPrompt();
        System.out.print(SET_TEXT_COLOR_BLUE + "Successfully logged in. Type 'help' to view the menu\n");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("Logged out. Type 'help' to view the menu")) {
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
    }

    private String executeCommand(String input) {
        var tokens = input.toLowerCase().split(" ");
//        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var cmd = tokens[0];
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        try {
            if (Objects.equals(cmd, "help")) {
                return help();
//                return "help not implemented";
            } else if (Objects.equals(cmd, "logout")) {
                return logout();
//                return "logout";
            } else if (Objects.equals(cmd, "create")) {
                return create(params);
//                return "create_game not implemented";
            } else if (Objects.equals(cmd, "list")) {
                return list();
//                return "help not implemented";
            } else if (Objects.equals(cmd, "join")) {
                return join(params);
//                return "help not implemented";
            } else if (Objects.equals(cmd, "observe")) {
                return observe(params);
//                return "help not implemented";
            }else {
                return "Invalid command. Try one of these: \n" + help();
            }
        } catch (ServiceException e) {
            return "Unexpected error" + e.getErrorMessage();
        }
//        printPrompt();
    }

    private String help() {
        return "create <NAME> - to create a new game\n" +
                "list - to list all games\n" +
                "join <ID> [WHITE|BLACK] - to join a game\n" +
                "observe <ID> - to observe a game\n" +
                "logout - to logout\n" +
//                "quit - to quit this program\n" +
                "help - to display this menu\n";
    }

    private String logout() throws ServiceException {
        serverFacade.logout(authToken);
        return "Logged out. Type 'help' to view the menu";
    }

    private String create(String[] params) throws ServiceException {
        if (params.length != 1) {
            return "Please enter a game name to create a game\n" +
                    "Example: create game_name\n";
        }
        CreateGameResult createData = serverFacade.createGame(authToken, params[0]);
        return "Game '" + params[0] + "' created, type 'list' to see all games\n";
    }

    private String list() throws ServiceException {
        ListGamesResult listData = serverFacade.listGames(authToken);
        return gameListToString(listData);
    }

    private String join(String[] params) throws ServiceException {
        list();
        if (params.length != 2 || !params[0].matches("\\d+") || !(params[1].equalsIgnoreCase("white") || params[1].equalsIgnoreCase("black"))) {
            return "Please enter a game number and choice of player color to join a game\n" +
                    "Example: join 1 WHITE\n";
        }
        if (gameNumbers.size() < Integer.parseInt(params[0])) {
            return "Invalid game number. Please choose a game from this list: \n" + list();
        }
        var game = gameNumbers.get(params[0]);
        String playerColor = params[1];
        try {
            serverFacade.joinGame(authToken, playerColor, game.gameID());
        } catch (ServiceException e) {
            if (Objects.equals(e.getErrorMessage(), "{\"message\": \"Error: Error: already taken\"}")) {
                return "Player color already taken. Please try again.\n" + list();
            }
        }
        new chessBoardUI(game.chessGame(), playerColor);
        return "";
    }

    private String observe(String[] params) throws ServiceException {
        if (params.length != 1 || !params[0].matches("\\d+")) {
            return "Please enter a game number to observer a game\n" +
                    "Example: observe 1\n";
        }

        if (gameNumbers.size() < Integer.parseInt(params[0])) {
            return "Invalid game number. Please choose a game from this list: \n" + list();
        }
        var game = gameNumbers.get(params[0]);
//        if (game == null) {
//        }
//        String playerColor = params[1];
//        serverFacade.joinGame(authToken, playerColor, game.gameID());
        new chessBoardUI(game.chessGame(), "white");
        return "";
    }

    private String gameListToString(ListGamesResult listData) {
        //"1 - MyGame, White Player: username, Black Player: AVAILABLE"
        gameNumbers = new HashMap<String, GameData>();
        String str = "";
        for (int i = 0; i < listData.games().size(); i++) {
            GameData game = listData.games().get(i);

            str += ((i+1) + " - ");
            str += (game.gameName() + ", ");
            if (game.whiteUsername() == null) {
                str += ("White Player: AVAILABLE, ");
            } else { str += ("White Player: " + game.whiteUsername() + ", "); }
            if (game.blackUsername() == null) {
                str += ("Black Player: AVAILABLE\n");
            } else { str += ("Black Player: " + game.blackUsername() + "\n"); }

            gameNumbers.put(Integer.toString(i+1), game);
        }
        return str;
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + "[LOGGED IN] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
