package server;

import com.google.gson.Gson;
import dataaccess.*;
import handler.*;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    private UserService userService;
//    private AuthService authService;
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
//        Spark.get("/pet", this::listPets);
//        Spark.delete("/pet/:id", this::deletePet);
//        Spark.delete("/pet", this::deleteAllPets);
//        Spark.exception(ResponseException.class, this::exceptionHandler);


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    //port method??
    //exception handler??

    private void setServices() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDao();
        GameDAO gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO, gameDAO);
//        authService = new AuthService(userDAO, authDAO, gameDAO);
        gameService = new GameService(userDAO, authDAO, gameDAO);
    }

    private Object register(Request req, Response res) {
        RegisterHandler handler = new RegisterHandler(req, res, userService);
        res.status(handler.getStatusCode());
        return handler.getResult();
//        if (result == null || statusCode == 0) {
//            res.status(500);
//            return "Error: unidentified";
//        }
    }

    private Object login(Request req, Response res) {
        LoginHandler handler = new LoginHandler(req, res, userService);
        res.status(handler.getStatusCode());
        return handler.getResult();
    }

    private Object logout(Request req, Response res) {
        LogoutHandler handler = new LogoutHandler(req, res, userService);
        res.status(handler.getStatusCode());
        return handler.getResult();
    }

    private Object createGame(Request req, Response res) {
        CreateGameHandler handler = new CreateGameHandler(req, res, gameService);
        res.status(handler.getStatusCode());
        return handler.getResult();
    }

    private Object joinGame(Request req, Response res) {
        JoinGameHandler handler = new JoinGameHandler(req, res, gameService);
        res.status(handler.getStatusCode());
        return handler.getResult();
    }

    private Object clear(Request req, Response res) {
        setServices(); //legal?
        res.status(200);
        return "";
    }

//    private Object register(Request req, Response res)  { //throws ResponseException
////        var pet = new Gson().fromJson(req.body(), Pet.class);
//        var pet = new Gson().fromJson(req.body(), RegisterHandler.class);
//        pet = service.addPet(pet);
////        webSocketHandler.makeNoise(pet.name(), pet.sound());
//        return new Gson().toJson(pet);
//    }

}
