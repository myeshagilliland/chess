import ui.PostLoginUI;
import ui.PreLoginUI;

import java.util.Objects;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final PreLoginUI preLoginUI;
    private PostLoginUI postLoginUI;

    public Repl(int port) {
        preLoginUI = new PreLoginUI(port); //, this);
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
        }
        System.out.println();
    }

    private void printPrompt(String ui) {
        String message;
        if (Objects.equals(ui, "preLogin")) {
            message = "[LOGGED OUT] >>> ";
        } else {
            message = "[LOGGED IN] >>> ";
        }
        System.out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + message + SET_TEXT_COLOR_GREEN);
    }
}
