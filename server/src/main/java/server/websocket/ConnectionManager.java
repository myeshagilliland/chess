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

    public void add(int gameID, String username, String role, Session session) {
        var connection = new Connection(username, role, session);
//        connections.computeIfAbsent(gameID, k -> new ArrayList<>()).add(connection);
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

    public void sendError (int gameID, String username, ErrorMessage errorMessage) throws IOException {
//        var removeList = new ArrayList<Connection>();
        Connection c = connections.get(gameID).get(username);
//        for (var c : connections.get(gameID).values()) {
//            if (c.session.isOpen()) {
//                if (!c.username.equals(excludeUsername)) {
                    c.send(new Gson().toJson(errorMessage));
//                }
//            } else {
//                removeList.add(c);
//            }
//        }

        // Clean up any connections that were left open.
//        for (var c : removeList) {
//            connections.get(gameID).remove(c.username);
//        }
    }


//    public void broadcast(String excludeVisitorName, Notification notification) throws IOException {
//        var removeList = new ArrayList<Connection>();
//        for (var c : connections.values()) {
//            if (c.session.isOpen()) {
//                if (!c.visitorName.equals(excludeVisitorName)) {
//                    c.send(notification.toString());
//                }
//            } else {
//                removeList.add(c);
//            }
//        }
//
//        // Clean up any connections that were left open.
//        for (var c : removeList) {
//            connections.remove(c.visitorName);
//        }
//    }
}
