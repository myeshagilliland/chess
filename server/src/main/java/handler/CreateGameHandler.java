package handler;

import com.google.gson.Gson;
import exception.ServiceException;
import requestresult.CreateGameRequest;
import requestresult.CreateGameResult;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {

    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) {
        CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        createGameRequest = new CreateGameRequest(req.headers("Authorization"), createGameRequest.gameName());

        try {
                CreateGameResult createGameResult = gameService.createGame(createGameRequest);
                res.status(200);
                return new Gson().toJson(createGameResult);
            } catch (ServiceException e) {
                res.status(e.getStatusCode());
                return e.getErrorMessage();
            }
    }
}
