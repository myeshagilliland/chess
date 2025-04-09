package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ServiceException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;
    String playerColor;

    public WebSocketFacade(int port, NotificationHandler notificationHandler) throws ServiceException {
        try {
            String url = String.format("ws://localhost:%d", port);
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
//                    ServerMessage notification = serverMessage;
                    switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION -> notificationHandler.sendNotification(new Gson().fromJson(message, NotificationMessage.class));
                        case LOAD_GAME -> notificationHandler.sendLoadGame(new Gson().fromJson(message, LoadGameMessage.class), playerColor);
                        case ERROR -> notificationHandler.sendError(new Gson().fromJson(message, ErrorMessage.class));
                    }
//                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID, String playerColor) throws ServiceException {
        this.playerColor = playerColor;

        try {
            var connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(connectCommand));
        } catch (IOException ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    public void leave(String authToken, int gameID) throws ServiceException {
        try {
            var leaveCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(leaveCommand));
        } catch (IOException ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws ServiceException {
        try {
            var resignCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(resignCommand));
        } catch (IOException ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ServiceException {
        try {
            var makeMoveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(makeMoveCommand));
        } catch (IOException ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

}
