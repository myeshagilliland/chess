package ui;

import exception.ServiceException;
import serverFacade.ServerFacade;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class postloginUI {

    private ServerFacade serverFacade;
    private String authToken;

    public postloginUI(ServerFacade serverFacade, String authToken) {
        this.serverFacade = serverFacade;
        this.authToken = authToken;

//        printPrompt();

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout")) {
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
//                return help();
                return "help not implemented";
            } else if (Objects.equals(cmd, "logout")) {
                return logout();
//                return "logout";
            } else if (Objects.equals(cmd, "create")) {
                return "create_game not implemented";
            } else if (Objects.equals(cmd, "list")) {
//                return listGames(params);
                return "help not implemented";
            } else if (Objects.equals(cmd, "join")) {
//                return playGame(params);
                return "help not implemented";
            } else if (Objects.equals(cmd, "observe")) {
//                return observeGame(params);
                return "help not implemented";
            }else {
                return "not implemented";
            }
        } catch (ServiceException e) {
            return "Unexpected error" + e.getErrorMessage();
        }
//        printPrompt();
    }

    private String logout() throws ServiceException {
        serverFacade.logout(authToken);
        return "logout";
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + "[LOGGED IN] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
