package websocket;

import com.google.gson.Gson;
import exception.ServiceException;
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

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ServiceException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
//                    ServerMessage.ServerMessageType type = serverMessage.getServerMessageType();
                    ServerMessage notification = serverMessage;
                    switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION -> notification = new Gson().fromJson(message, NotificationMessage.class);
                        case LOAD_GAME -> notification = new Gson().fromJson(message, LoadGameMessage.class);
                        case ERROR -> notification = new Gson().fromJson(message, ErrorMessage.class);
                    }
                    notificationHandler.notify(notification);
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

    public void connect(String authToken, int gameID) throws ServiceException {
        try {
            var connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(connectCommand));
        } catch (IOException ex) {
            throw new ServiceException(ex.getMessage());
        }
    }
//
//    public void leavePetShop(String visitorName) throws ServiceException {
//        try {
////            var action = new UserGameCommand(UserGameCommand.CommandType.EXIT, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//            this.session.close();
//        } catch (IOException ex) {
//            throw new ServiceException(ex.getMessage());
//        }
//    }

}
