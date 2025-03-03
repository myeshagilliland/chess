package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requestresult.ListGamesRequest;
import requestresult.ListGamesResult;
import service.*;
import spark.Request;

public class ListGamesHandler {

    private String result;
    private int statusCode;

    public ListGamesHandler(Request req, GameService service) {
        ListGamesRequest listGamesRequest = new ListGamesRequest(req.headers("Authorization"));

        try {
            ListGamesResult listGamesResult = service.listGames(listGamesRequest);
            result = new Gson().toJson(listGamesResult);
            statusCode = 200;
        } catch (DataAccessException e) {
            ErrorMessage error = new ErrorMessage(e.getMessage());
            result = new Gson().toJson(error);
            statusCode = 401;
        }

    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResult() {
        return result;
    }

}
