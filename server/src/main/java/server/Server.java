package server;

import com.google.gson.Gson;
import handler.RegisterHandler;
import service.UserService;
import spark.*;

public class Server {

    private UserService userService = new UserService();
//    private AuthService authService = new AuthService();
//    private GameService gameService = new GameService();


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
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

    private Object register(Request req, Response res) {
        RegisterHandler handler = new RegisterHandler(req, res, userService);
        res.status(handler.getStatusCode());
        return handler.getResult();
//        if (result == null || statusCode == 0) {
//            res.status(500);
//            return "Error: unidentified";
//        }
    }

//    private Object register(Request req, Response res)  { //throws ResponseException
////        var pet = new Gson().fromJson(req.body(), Pet.class);
//        var pet = new Gson().fromJson(req.body(), RegisterHandler.class);
//        pet = service.addPet(pet);
////        webSocketHandler.makeNoise(pet.name(), pet.sound());
//        return new Gson().toJson(pet);
//    }

}
