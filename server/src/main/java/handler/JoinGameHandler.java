package handler;

import com.google.gson.Gson;
import exception.ServiceException;
import requestresult.JoinGameRequest;
import requestresult.JoinGameResult;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {

    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) {
        JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
        joinGameRequest = new JoinGameRequest(req.headers("Authorization"),
                joinGameRequest.playerColor(), joinGameRequest.gameID());

        try {
            JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest);
            res.status(200);
            return new Gson().toJson(joinGameResult);
        } catch (ServiceException e) {
            res.status(e.getStatusCode());
            return e.getErrorMessage();
        }
    }

}