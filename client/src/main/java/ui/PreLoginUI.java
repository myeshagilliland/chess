package ui;

import exception.ServiceException;
import requestresult.LoginResult;
import requestresult.RegisterResult;
import facade.ServerFacade;
import websocket.NotificationHandler;

import java.util.Arrays;
import java.util.Objects;
import static ui.EscapeSequences.*;

public class PreLoginUI {

    private int port;
    private ServerFacade serverFacade;
    private PostLoginUI postLoginUI;
    private NotificationHandler notificationHandler;

    public PreLoginUI(int port, NotificationHandler notificationHandler) {
        this.port = port;
        this.serverFacade = new ServerFacade(port);
        this.notificationHandler = notificationHandler;
    }

    public String executeCommand(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = tokens[0];
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        try {
            return switch (cmd) {
                case "help" -> help();
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                case null, default -> "Invalid command. Try one of these: \n" + help();
            };
        } catch (ServiceException e) {
            return "Unexpected error" + e.getErrorMessage() + "\n";
        }
    }

    public PostLoginUI getPostLoginUI() {
        return postLoginUI;
    }

    private String help() {
        return "register <USERNAME> <PASSWORD> <EMAIL> : to create an account\n" +
                "login <USERNAME> <PASSWORD> : to login to your account\n" +
                "quit : to exit chess\n" +
                "help : to view this menu\n";
    }

    private String register(String[] params) throws ServiceException {
        if (params.length != 3) {
            return "Please enter a username, password, and email to register.\n" +
                    "Example: register username password email\n";
        }
        RegisterResult registerData = null;
        try {
            registerData = serverFacade.register(params[0], params[1], params[2]);
        } catch (ServiceException e) {
            if (Objects.equals(e.getErrorMessage(), "{\"message\": \"Error: Error: already taken\"}")) {
                return "Username already taken. Please try again\n";
            }
        }
        postLoginUI = new PostLoginUI(port, serverFacade, notificationHandler, registerData.authToken());
        return "Logged in";
    }

    private String login(String[] params) throws ServiceException {
        if (params.length != 2) {
            return "Please enter a username and password to login.\n" +
                    "Example: login username password\n";
        }
        LoginResult loginData = null;
        try {
            loginData = serverFacade.login(params[0], params[1]);
        } catch (ServiceException e) {
            if (Objects.equals(e.getErrorMessage(), "{\"message\": \"Error: Error: unauthorized\"}")) {
                return "User not registered. Please try again.\n";
            }
        }
        postLoginUI = new PostLoginUI(port, serverFacade, notificationHandler, loginData.authToken());
        return "Logged in";
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + "[LOGGED OUT] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
