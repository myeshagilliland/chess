package websocket;

import chess.ChessGame;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void sendNotification(NotificationMessage message);
    void sendError(ErrorMessage error);
    void sendLoadGame(LoadGameMessage loadGameMessage, String playerColor);
//    ChessGame getGame();
}
