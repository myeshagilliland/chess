package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requestresult.CreateGameRequest;
import requestresult.CreateGameResult;
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
//
//    private String result;
//    private int statusCode;
//
//    public ListGamesHandler(Request req, GameService service) {
//        ListGamesRequest listGamesRequest = new ListGamesRequest(req.headers("Authorization"));
//
//        try {
//            ListGamesResult listGamesResult = service.listGames(listGamesRequest);
//            result = new Gson().toJson(listGamesResult);
//            statusCode = 200;
//        } catch (DataAccessException e) {
//            ErrorMessage error = new ErrorMessage(e.getMessage());
//            result = new Gson().toJson(error);
//            statusCode = 401;
//        }
//
//    }
//
//    public int getStatusCode() {
//        return statusCode;
//    }
//
//    public String getResult() {
//        return result;
//    }
//
//}
