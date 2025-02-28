package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.*;
import spark.Request;
import spark.Response;

public class CreateGameHandler {

    private String result;
    private int statusCode;

    public CreateGameHandler(Request req, Response res, GameService service) {
        CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        createGameRequest = new CreateGameRequest(req.headers("Authorization"), createGameRequest.gameName());
//        System.out.println(createGameRequest);

        if (createGameRequest.authToken() == null || createGameRequest.gameName() == null) {
            result = new Gson().toJson("Error: bad request");
            statusCode = 400;
        } else {
            try {
                CreateGameResult createGameResult = service.createGame(createGameRequest);
                result = new Gson().toJson(createGameResult);
                statusCode = 200;
            } catch (DataAccessException e) {
                ErrorMessage error = new ErrorMessage(e.getMessage());
                result = new Gson().toJson(error);
                statusCode = 401;
            }
        }

    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResult() {
        return result;
    }

}
