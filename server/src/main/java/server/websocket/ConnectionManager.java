package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Connection>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, String username, Session session) {
        var connection = new Connection(username, session);
        connections.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>()).put(username, connection);
    }

    public void remove(int gameID, String username) {
        connections.get(gameID).remove(username);
    }

    public void sendNotification (int gameID, String excludeUsername, NotificationMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.get(gameID).values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludeUsername)) {
                    c.send(new Gson().toJson(notification));
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.get(gameID).remove(c.username);
        }
    }

    public void sendLoadGame (int gameID, String excludeUsername, LoadGameMessage loadGameMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.get(gameID).values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludeUsername)) {
                    c.send(new Gson().toJson(loadGameMessage));
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.get(gameID).remove(c.username);
        }
    }

    public void sendLoadGameRoot (Session session, LoadGameMessage loadGameMessage) throws IOException {
        session.getRemote().sendString(new Gson().toJson(loadGameMessage));
    }


    public void sendError (Session session, ErrorMessage errorMessage) throws IOException {
        session.getRemote().sendString(new Gson().toJson(errorMessage));
    }

}
