package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ServiceException;
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

    private AuthData getAndCheckAuthData (String authToken, Session session) throws IOException, ServiceException {
        AuthData authData = null;
        try {
            authData = authDAO.findAuth(authToken);
        } catch (DataAccessException e) {
            connections.sendError(session, new ErrorMessage(ERROR, e.getMessage()));
        }

        if (authData == null) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Not logged in. Please try again");
            connections.sendError(session, errorMessage);
            throw new ServiceException("auth error");
        }

        return authData;
    }

    private GameData getAndCheckGameData (int gameID, Session session) throws IOException, ServiceException {
        GameData gameData = null;
        try {
            gameData = gameDAO.findGame(gameID);
        } catch (DataAccessException e) {
            connections.sendError(session, new ErrorMessage(ERROR, e.getMessage()));
        }
        if (gameData == null) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Game not found. Please try again");
            connections.sendError(session, errorMessage);
            throw new ServiceException("game error");
        }

        return gameData;
    }

    private void connect(String authToken, int gameID, Session session) throws IOException {

        AuthData authDataC;
        GameData gameDataC;
        try {
            authDataC = getAndCheckAuthData(authToken, session);
            gameDataC = getAndCheckGameData(gameID, session);
        } catch (ServiceException e) {
            return;
        }

        connections.add(gameID, authDataC.username(), session);

        String role = "observer";
        if (Objects.equals(authDataC.username(), gameDataC.whiteUsername())) {
            role = "white";
        } else if (Objects.equals(authDataC.username(), gameDataC.blackUsername())) {
            role = "black";
        }

        var message = String.format("%s joined as %s", authDataC.username(), role);
        var notification = new NotificationMessage(NOTIFICATION, message);
        var loadGame = new LoadGameMessage(LOAD_GAME, gameDataC.chessGame());

        connections.sendLoadGameRoot(session, loadGame);
        connections.sendNotification(gameID, authDataC.username(), notification);
    }

    private void leave(String authToken, int gameID, Session session) throws IOException {

        AuthData authDataL;
        GameData gameDataL;
        try {
            authDataL = getAndCheckAuthData(authToken, session);
            gameDataL = getAndCheckGameData(gameID, session);
        } catch (ServiceException e) {
            return;
        }

        GameData updatedGameData = null;
        if (Objects.equals(authDataL.username(), gameDataL.whiteUsername())) {
            updatedGameData = new GameData(gameDataL.gameID(), null, gameDataL.blackUsername(),
                    gameDataL.gameName(), gameDataL.chessGame());
        } else if (Objects.equals(authDataL.username(), gameDataL.blackUsername())) {
            updatedGameData = new GameData(gameDataL.gameID(), gameDataL.whiteUsername(), null,
                    gameDataL.gameName(), gameDataL.chessGame());
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

        connections.remove(gameID, authDataL.username());

        var message = String.format("%s has left the game", authDataL.username());
        var notification = new NotificationMessage(NOTIFICATION, message);
        connections.sendNotification(gameID, authDataL.username(), notification);
    }

    private void resign(String authToken, int gameID, Session session) throws IOException {

        AuthData authDataR;
        GameData gameDataR;
        try {
            authDataR = getAndCheckAuthData(authToken, session);
            gameDataR = getAndCheckGameData(gameID, session);
        } catch (ServiceException e) {
            return;
        }

        if (!Objects.equals(authDataR.username(), gameDataR.whiteUsername()) &&
                !Objects.equals(authDataR.username(), gameDataR.blackUsername())) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Observer may not resign");
            connections.sendError(session, errorMessage);
            return;
        }

        if (gameDataR.chessGame().isOver()) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Game already over");
            connections.sendError(session, errorMessage);
            return;
        }

        gameDataR.chessGame().endGame();

        try {
            gameDAO.updateGame(gameDataR);
        } catch (DataAccessException e) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, e.getMessage());
            connections.sendError(session, errorMessage);
            return;
        }

        var message = String.format("GAME OVER: %s has resigned", authDataR.username());
        var notification = new NotificationMessage(NOTIFICATION, message);
        connections.sendNotification(gameID, null, notification);
    }

    private void makeMove(ChessMove move, String authToken, int gameID, Session session) throws IOException {

        AuthData authDataM;
        GameData gameDataM;
        try {
            authDataM = getAndCheckAuthData(authToken, session);
            gameDataM = getAndCheckGameData(gameID, session);
        } catch (ServiceException e) {
            return;
        }

        System.out.println(gameDataM.chessGame().getBoard().getPiece(move.getStartPosition()));

        if (gameDataM.chessGame().getBoard().getPiece(move.getStartPosition()) == null) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: No piece at position");
            connections.sendError(session, errorMessage);
            return;
        }

        ChessGame.TeamColor teamColor = null;
        ChessGame.TeamColor otherTeamColor = null;
        String otherUser = null;
        if (Objects.equals(authDataM.username(), gameDataM.whiteUsername())) {
            teamColor = ChessGame.TeamColor.WHITE;
            otherTeamColor = ChessGame.TeamColor.BLACK;
            otherUser = gameDataM.blackUsername();
        } else if (Objects.equals(authDataM.username(), gameDataM.blackUsername())) {
            teamColor = ChessGame.TeamColor.BLACK;
            otherTeamColor = ChessGame.TeamColor.WHITE;
            otherUser = gameDataM.whiteUsername();
        } else {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Observer may not move");
            connections.sendError(session, errorMessage);
            return;
        }

        if (!(teamColor == gameDataM.chessGame().getBoard().getPiece(move.getStartPosition()).getTeamColor())) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: You can only move your own pieces");
            connections.sendError(session, errorMessage);
            return;
        }

        if (gameDataM.chessGame().isOver()) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error: Game already over");
            connections.sendError(session, errorMessage);
            return;
        }

        try {
            gameDataM.chessGame().makeMove(move);
        } catch (InvalidMoveException e) {
            var message = String.format("Error: %s", e.getMessage());
            ErrorMessage errorMessage = new ErrorMessage(ERROR, message);
            connections.sendError(session, errorMessage);
            return;
        }

        try {
            gameDAO.updateGame(gameDataM);
        } catch (DataAccessException e) {
            ErrorMessage errorMessage = new ErrorMessage(ERROR, e.getMessage());
            connections.sendError(session, errorMessage);
            return;
        }

        var loadGame = new LoadGameMessage(LOAD_GAME, gameDataM.chessGame());
        connections.sendLoadGame(gameID, null, loadGame);

        ChessPiece.PieceType pieceType =
                gameDataM.chessGame().getBoard().getPiece(move.getEndPosition()).getPieceType();
        String finalPosition = formatPosition(move.getEndPosition(), teamColor);
        var message = String.format("%s moved %s %s to %s", authDataM.username(), teamColor, pieceType, finalPosition);
        var notification = new NotificationMessage(NOTIFICATION, message);
        connections.sendNotification(gameID, authDataM.username(), notification);

        String statusMessage = "";
        if (gameDataM.chessGame().isInCheckmate(otherTeamColor)) {
            gameDataM.chessGame().endGame();
            statusMessage = String.format("GAME OVER: %s is in check mate. %s wins!", otherUser, authDataM.username());
        } else if (gameDataM.chessGame().isInStalemate(otherTeamColor)) {
            gameDataM.chessGame().endGame();
            statusMessage = String.format("GAME OVER: %s is in check mate. %s wins!", otherUser, authDataM.username());
        } else if (gameDataM.chessGame().isInCheck(otherTeamColor)) {
            statusMessage = String.format("%s is in check", otherUser);
        }

        try {
            gameDAO.updateGame(gameDataM);
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
        return pos;
    }

}
