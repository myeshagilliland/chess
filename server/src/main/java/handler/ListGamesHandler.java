package handler;

import com.google.gson.Gson;
import exception.ServiceException;
import requestresult.ListGamesRequest;
import requestresult.ListGamesResult;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {

    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) {
        ListGamesRequest listGamesRequest = new ListGamesRequest(req.headers("Authorization"));

        try {
            ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);
            res.status(200);
            return new Gson().toJson(listGamesResult);
        } catch (ServiceException e) {
            res.status(e.getStatusCode());
            return e.getErrorMessage();
        }
    }
}