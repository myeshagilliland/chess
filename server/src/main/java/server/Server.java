package server;

import dataaccess.*;
import handler.*;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    private UserService userService;
    private GameService gameService;

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        setServices();

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.delete("/db", this::clear);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.get("/game", this::listGames);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void setServices() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDao();
        GameDAO gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO, gameDAO);
        gameService = new GameService(userDAO, authDAO, gameDAO);
    }

    private Object register(Request req, Response res) {
        RegisterHandler handler = new RegisterHandler(req, userService);
        res.status(handler.getStatusCode());
        return handler.getResult();
    }

    private Object login(Request req, Response res) {
        LoginHandler handler = new LoginHandler(req, userService);
        res.status(handler.getStatusCode());
        return handler.getResult();
    }

    private Object logout(Request req, Response res) {
        LogoutHandler handler = new LogoutHandler(req, userService);
        res.status(handler.getStatusCode());
        return handler.getResult();
    }

    private Object createGame(Request req, Response res) {
        CreateGameHandler handler = new CreateGameHandler(req, gameService);
        res.status(handler.getStatusCode());
        return handler.getResult();
    }

    private Object joinGame(Request req, Response res) {
        JoinGameHandler handler = new JoinGameHandler(req, gameService);
        res.status(handler.getStatusCode());
        return handler.getResult();
    }

    private Object listGames(Request req, Response res) {
        ListGamesHandler handler = new ListGamesHandler(req, gameService);
        res.status(handler.getStatusCode());
        return handler.getResult();
    }

    private Object clear(Request req, Response res) {
        ClearHandler handler = new ClearHandler(req, userService);
        res.status(handler.getStatusCode());
        return handler.getResult();
    }

}
