package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ServiceException;
import model.GameData;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.Map;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class GameUI {
    private WebSocketFacade webSocketFacade;
    private GameData gameData;
    private ChessGame chessGame;
    private String playerColor;
    private String authToken;
    private static final Map<String, Integer> MOVE_CONVERTER = Map.of(
            "a", 1,
            "b", 2,
            "c", 3,
            "d", 4,
            "e", 5,
            "f", 6,
            "g", 7,
            "h", 8
    );

    public GameUI(int port, NotificationHandler nHandler, GameData game, String playerColor, String authToken) {
        this.gameData = game;
        this.playerColor = playerColor;
        this.authToken = authToken;

        try {
            this.webSocketFacade = new WebSocketFacade(port, nHandler);
            webSocketFacade.connect(authToken, gameData.gameID(), playerColor);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }

        System.out.print(SET_TEXT_COLOR_BLUE + "Game started. Type 'help' to view the menu\n");
    }

    public String executeCommand(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = tokens[0];
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
                case "yes" -> yes();
                case "no" -> no();
                case "moves" -> moves(params);
                case "quit" -> "quit";
                case null, default -> "Invalid command. Try one of these: \n" + help();
            };
    }

    private String help() {
        return "move <CURRENT POSITION> <FINAL POSITION> <optional: PROMOTION PIECE>: to move a piece\n" +
                "moves <POSITION> : to highlight legal moves for piece at position\n" +
                "redraw : to redisplay current game board\n" +
                "resign : to forfeit current game\n" +
                "leave : to leave current game\n" +
                "quit : to exit chess\n" +
                "help : to display this menu\n";
    }

    private String leave() {
        try {
            webSocketFacade.leave(authToken, gameData.gameID());
        } catch (ServiceException e) {
            return "Error: " + e.getErrorMessage();
        }
        return "Game ended";
    }

    private String resign() {
        return "Are you sure you want to resign? Type yes/no\n";
    }

    private String yes() {
        try {
            webSocketFacade.resign(authToken, gameData.gameID());
        } catch (ServiceException e) {
            return "Error: " + e.getErrorMessage();
        }
        return "Game resumed. Type 'help' to view the menu";
    }

    private String no() {
        return "";
    }

    private String move(String[] params) {
        if (params.length < 2) {
            return "Please enter a start and end position to move\n" +
                    "Example: move a1 a2 <optional: PROMOTION PIECE>\n";
        }
        ChessPiece.PieceType promotionPiece = null;
        if (params.length == 3) {
            promotionPiece = ChessPiece.PieceType.valueOf(params[2].toUpperCase());
        }

        ChessPosition startPosition;
        ChessPosition endPosition;
        try {
            startPosition = getChessPosition(params[0]);
            endPosition = getChessPosition(params[1]);
        } catch (ServiceException e) {
            return e.getErrorMessage();
        }

        ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece); //FIX THIS FOR PAWN

        try {
            webSocketFacade.makeMove(authToken, gameData.gameID(), move);
        } catch (ServiceException e) {
            return "Error: " + e.getMessage();
        }

        return "";
    }

    private ChessPosition getChessPosition(String position) throws ServiceException {
        ServiceException serviceException = new ServiceException("Please enter a valid position\n" +
                "Example: a1\n");

        if (position.length() != 2) {
            throw serviceException;
        }

        int row = Character.getNumericValue(position.charAt(1));
        var col = MOVE_CONVERTER.get(String.valueOf(position.charAt(0)));

        if (row > 8 || row < 1 || col == null) {
            throw serviceException;
        }

        return new ChessPosition(row, col);
    }

    private String redraw() {
        new ChessBoardUI(chessGame, playerColor).printBoard();
        return "";
    }

    private String moves(String[] params) {
        if (params.length != 1) {
            return "Please enter a position see possible moves\n" +
                    "Example: move a1\n";
        }

        ChessPosition position;
        try {
            position = getChessPosition(params[0]);
        } catch (ServiceException e) {
            return e.getErrorMessage();
        }

        if (chessGame.getBoard().getPiece(position) == null) {
            return "No piece at position. Please try again\n";
        }

        new ChessBoardUI(chessGame, playerColor).highlightMoves(position);
        return "";
    }

    public void updateGame (ChessGame game) {
        this.chessGame = game;
    }

}
