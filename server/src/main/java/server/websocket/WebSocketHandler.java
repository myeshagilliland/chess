package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static websocket.messages.ServerMessage.ServerMessageType.*;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private ArrayList<Integer> inactiveGames = new ArrayList<>();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            MakeMoveCommand moveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
            makeMove(moveCommand.getMove(), moveCommand.getAuthToken(), moveCommand.getGameID(), session);
        } else {
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getAuthToken(), command.getGameID(), session);
                case LEAVE -> leave(command.getAuthToken(), command.getGameID(), session);
                case RESIGN -> resign(command.getAuthToken(), command.getGameID(), session);
            }
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException {

        AuthData authData = null;
        try {
            authData = authDAO.findAuth(authToken);
        } catch (DataAccessException e) {
            connections.sendError(session, new ErrorMessage(ERROR, e.getMessage()));
        }
        if (authData == null) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Not logged in. Please try again");
            connections.sendError(session, errorMessage);
            return;
        }

        GameData gameData = null;
        try {
            gameData = gameDAO.findGame(gameID);
        } catch (DataAccessException e) {
            connections.sendError(session, new ErrorMessage(ERROR, e.getMessage()));
        }
        if (gameData == null) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Game not found. Please try again");
            connections.sendError(session, errorMessage);
            return;
        }

        connections.add(gameID, authData.username(), session);

        String role = "observer";
        if (Objects.equals(authData.username(), gameData.whiteUsername())) {
            role = "white";
        } else if (Objects.equals(authData.username(), gameData.blackUsername())) {
            role = "black";
        }

        var message = String.format("%s joined as %s", authData.username(), role);
        var notification = new NotificationMessage(NOTIFICATION, message);
        var loadGame = new LoadGameMessage(LOAD_GAME, gameData.chessGame());

        connections.sendLoadGameRoot(session, loadGame);
        connections.sendNotification(gameID, authData.username(), notification);
    }

    private void leave(String authToken, int gameID, Session session) throws IOException {

        AuthData authData = null;
        try {
            authData = authDAO.findAuth(authToken);
        } catch (DataAccessException e) {
            connections.sendError(session, new ErrorMessage(ERROR, e.getMessage()));
        }
        if (authData == null) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Not logged in. Please try again");
            connections.sendError(session, errorMessage);
            return;
        }

        GameData gameData = null;
        try {
            gameData = gameDAO.findGame(gameID);
        } catch (DataAccessException e) {
            connections.sendError(session, new ErrorMessage(ERROR, e.getMessage()));
        }
        if (gameData == null) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Game not found. Please try again");
            connections.sendError(session, errorMessage);
            return;
        }

        GameData updatedGameData = null;
        if (Objects.equals(authData.username(), gameData.whiteUsername())) {
            updatedGameData = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.chessGame());
        } else if (Objects.equals(authData.username(), gameData.blackUsername())) {
            updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.chessGame());
        }

        if (updatedGameData != null) {
            try {
                gameDAO.updateGame(updatedGameData);
            } catch (DataAccessException e) {
                ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: user not in game");
                connections.sendError(session, errorMessage);
                return;
            }
        }

        connections.remove(gameID, authData.username());

        var message = String.format("%s has left the game", authData.username());
        var notification = new NotificationMessage(NOTIFICATION, message);
        connections.sendNotification(gameID, authData.username(), notification);
    }

    private void resign(String authToken, int gameID, Session session) throws IOException {

        AuthData authData = null;
        try {
            authData = authDAO.findAuth(authToken);
        } catch (DataAccessException e) {
            connections.sendError(session, new ErrorMessage(ERROR, e.getMessage()));
        }
        if (authData == null) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Not logged in. Please try again");
            connections.sendError(session, errorMessage);
            return;
        }

        GameData gameData = null;
        try {
            gameData = gameDAO.findGame(gameID);
        } catch (DataAccessException e) {
            connections.sendError(session, new ErrorMessage(ERROR, e.getMessage()));
        }
        if (gameData == null) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Game not found. Please try again");
            connections.sendError(session, errorMessage);
            return;
        }

        if (!Objects.equals(authData.username(), gameData.whiteUsername()) &&
                !Objects.equals(authData.username(), gameData.blackUsername())) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Observer may not resign");
            connections.sendError(session, errorMessage);
            return;
        }

        if (gameData.chessGame().isOver()) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Game already over");
            connections.sendError(session, errorMessage);
            return;
        }

        gameData.chessGame().endGame();

        try {
            gameDAO.updateGame(gameData);
        } catch (DataAccessException e) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, e.getMessage());
            connections.sendError(session, errorMessage);
            return;
        }

        var message = String.format("GAME OVER: %s has resigned", authData.username());
        var notification = new NotificationMessage(NOTIFICATION, message);
        connections.sendNotification(gameID, null, notification);
    }

    private void makeMove(ChessMove move, String authToken, int gameID, Session session) throws IOException {

        AuthData authData = null;
        try {
            authData = authDAO.findAuth(authToken);
        } catch (DataAccessException e) {
            connections.sendError(session, new ErrorMessage(ERROR, e.getMessage()));
        }
        if (authData == null) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Not logged in. Please try again");
            connections.sendError(session, errorMessage);
            return;
        }

        GameData gameData = null;
        try {
            gameData = gameDAO.findGame(gameID);
        } catch (DataAccessException e) {
            connections.sendError(session, new ErrorMessage(ERROR, e.getMessage()));
        }
        if (gameData == null) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Game not found. Please try again");
            connections.sendError(session, errorMessage);
            return;
        }


        ChessGame.TeamColor teamColor = null;
        ChessGame.TeamColor otherTeamColor = null;
        String otherUser = null;
        if (Objects.equals(authData.username(), gameData.whiteUsername())) {
            teamColor = ChessGame.TeamColor.WHITE;
            otherTeamColor = ChessGame.TeamColor.BLACK;
            otherUser = gameData.blackUsername();
        } else if (Objects.equals(authData.username(), gameData.blackUsername())) {
            teamColor = ChessGame.TeamColor.BLACK;
            otherTeamColor = ChessGame.TeamColor.WHITE;
            otherUser = gameData.whiteUsername();
        } else {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Observer may not move");
            connections.sendError(session, errorMessage);
            return;
        }

        if (!(teamColor == gameData.chessGame().getBoard().getPiece(move.getStartPosition()).getTeamColor())) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: You can only move your own pieces");
            connections.sendError(session, errorMessage);
            return;
        }

        if (gameData.chessGame().isOver()) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Game already over");
            connections.sendError(session, errorMessage);
            return;
        }

        try {
            gameData.chessGame().makeMove(move);
        } catch (InvalidMoveException e) {
            var message = String.format("Error: %s", e.getMessage());
            ErrorMessage errorMessage = new ErrorMessage(ERROR, message);
            connections.sendError(session, errorMessage);
            return;
        }

        try {
            gameDAO.updateGame(gameData);
        } catch (DataAccessException e) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, e.getMessage());
            connections.sendError(session, errorMessage);
            return;
        }

        var loadGame = new LoadGameMessage(LOAD_GAME, gameData.chessGame());
        connections.sendLoadGame(gameID, null, loadGame);

        ChessPiece.PieceType pieceType = gameData.chessGame().getBoard().getPiece(move.getEndPosition()).getPieceType();
        String finalPosition = formatPosition(move.getEndPosition(), teamColor);
        var message = String.format("%s moved %s %s to %s", authData.username(), teamColor, pieceType, finalPosition);
        var notification = new NotificationMessage(NOTIFICATION, message);
        connections.sendNotification(gameID, authData.username(), notification);

        String statusMessage = "";
        if (gameData.chessGame().isInCheckmate(otherTeamColor)) {
            gameData.chessGame().endGame();
            statusMessage = String.format("GAME OVER: %s is in check mate. %s wins!", otherUser, authData.username());
        } else if (gameData.chessGame().isInStalemate(otherTeamColor)) {
            gameData.chessGame().endGame();
            statusMessage = String.format("GAME OVER: %s is in check mate. %s wins!", otherUser, authData.username());
        } else if (gameData.chessGame().isInCheck(otherTeamColor)) {
            statusMessage = String.format("%s is in check", otherUser);
        }

        try {
            gameDAO.updateGame(gameData);
        } catch (DataAccessException e) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, e.getMessage());
            connections.sendError(session, errorMessage);
            return;
        }

        if (!statusMessage.isEmpty()) {
            var notification2 = new NotificationMessage(NOTIFICATION, statusMessage);
            connections.sendNotification(gameID, null, notification2);
        }
    }

    private String formatPosition(ChessPosition chessPosition, ChessGame.TeamColor teamColor) {
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String pos;
        pos = String.format("%s%d", letters[chessPosition.getColumn() - 1], chessPosition.getRow());
//        if (teamColor == ChessGame.TeamColor.BLACK) {
//            pos = String.format("%s%d", letters[chessPosition.getColumn() - 1], chessPosition.getRow());
//        } else {
//            pos = String.format("%s%d", letters[chessPosition.getColumn() - 1], chessPosition.getRow());
//        }
        return pos;
    }

}
