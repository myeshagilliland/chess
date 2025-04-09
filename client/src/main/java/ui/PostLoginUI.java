package ui;

import exception.ServiceException;
import model.GameData;
import requestresult.CreateGameResult;
import requestresult.ListGamesResult;
import facade.ServerFacade;
import websocket.NotificationHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import static ui.EscapeSequences.*;

public class PostLoginUI {

    private int port;
    private ServerFacade serverFacade;
    private NotificationHandler notificationHandler;
    private GameUI gameUI;
    private String authToken;
    private HashMap<String, GameData> createdGames = new HashMap<>();

    public PostLoginUI(int port, ServerFacade serverFacade, NotificationHandler notificationHandler, String authToken) {
        this.port = port;
        this.serverFacade = serverFacade;
        this.notificationHandler = notificationHandler;
        this.authToken = authToken;

        System.out.print(SET_TEXT_COLOR_BLUE + "Successfully logged in. Type 'help' to view the menu\n");
    }

    public String executeCommand(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = tokens[0];
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        try {
            return switch (cmd) {
                case "help" -> help();
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "quit" -> "quit";
                case null, default -> "Invalid command. Try one of these: \n" + help();
            };
        } catch (ServiceException e) {
            return "Unexpected error" + e.getErrorMessage() + "\n";
        }
    }

    public GameUI getGameUI() {
        return gameUI;
    }

    private String help() {
        return "create <NAME> : to create a new game\n" +
                "list : to list all games\n" +
                "join <ID> [WHITE|BLACK] : to join a game\n" +
                "observe <ID> : to observe a game\n" +
                "logout : to logout\n" +
                "quit : to exit chess\n" +
                "help : to display this menu\n";
    }

    private String logout() throws ServiceException {
        serverFacade.logout(authToken);
        return "Logged out";
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
        if (createdGames.size() < Integer.parseInt(params[0])) {
            return "Invalid game number. Please choose a game from this list: \n" + list();
        }
        var game = createdGames.get(params[0]);
        String playerColor = params[1];
        try {
            serverFacade.joinGame(authToken, playerColor, game.gameID());
        } catch (ServiceException e) {
            if (Objects.equals(e.getErrorMessage(), "{\"message\": \"Error: Error: already taken\"}")) {
                return "Player color already taken. Please try again.\n" + list();
            }
        }
        gameUI = new GameUI(port, notificationHandler, serverFacade, createdGames, game, playerColor, authToken);
        return "Game started";
    }

    private String observe(String[] params) throws ServiceException {
        if (params.length != 1 || !params[0].matches("\\d+")) {
            return "Please enter a game number to observer a game\n" +
                    "Example: observe 1\n";
        }

        if (createdGames.size() < Integer.parseInt(params[0])) {
            return "Invalid game number. Please choose a game from this list: \n" + list();
        }
        var game = createdGames.get(params[0]);
//        new ChessBoardUI(game.chessGame(), "white"); //FIX THIS!!!!!!!!!
        gameUI = new GameUI(port, notificationHandler, serverFacade, createdGames, game, "white", authToken);
        return "Game started";
    }

    private String gameListToString(ListGamesResult listData) {
        //"1 - MyGame, White Player: username, Black Player: AVAILABLE"
        createdGames = new HashMap<String, GameData>();
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

            createdGames.put(Integer.toString(i+1), game);
        }
        return str;
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + "[LOGGED IN] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
