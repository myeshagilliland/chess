package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.*;
import spark.Request;
import spark.Response;

public class ListGamesHandler {

    private String result;
    private int statusCode;

    public ListGamesHandler(Request req, GameService service) {
//        System.out.println(req.headers());
//        System.out.println(req.headers("Authorization"));
        ListGamesRequest listGamesRequest = new ListGamesRequest(req.headers("Authorization"));
//        LogoutRequest logoutRequest = new Gson().fromJson(req.headers("Authorization"), LogoutRequest.class);
        System.out.println(listGamesRequest);

//        if (loginRequest.username() == null || loginRequest.password() == null) {
//            result = new Gson().toJson("Error: bad request");
//            statusCode = 401;
//        } else {
        try {
            ListGamesResult listGamesResult = service.listGames(listGamesRequest);
            result = new Gson().toJson(listGamesResult); //error here
            System.out.println(result);
            statusCode = 200;
        } catch (DataAccessException e) {
            ErrorMessage error = new ErrorMessage(e.getMessage());
            result = new Gson().toJson(error);
            statusCode = 401;
        }
//        }

    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResult() {
        return result;
    }

}
