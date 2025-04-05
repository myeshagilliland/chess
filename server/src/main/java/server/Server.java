package server;

import dataaccess.*;
import handler.*;
import server.websocket.WebSocketHandler;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserService userService;
    private GameService gameService;

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        setServices();
        WebSocketHandler webSocketHandler = new WebSocketHandler(authDAO, gameDAO);

        Spark.webSocket("/ws", webSocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", new RegisterHandler(userService));
        Spark.delete("/db", new ClearHandler(userService));
        Spark.post("/session", new LoginHandler(userService));
        Spark.delete("/session", new LogoutHandler(userService));
        Spark.post("/game", new CreateGameHandler(gameService));
        Spark.put("/game", new JoinGameHandler(gameService));
        Spark.get("/game", new ListGamesHandler(gameService));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void setServices() {
        UserDAO userDAO = null;
        AuthDAO authDAO = null;
        GameDAO gameDAO = null;
        try {
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        userService = new UserService(userDAO, authDAO, gameDAO);
        gameService = new GameService(userDAO, authDAO, gameDAO);
    }

}
