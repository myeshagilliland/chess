package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.*;
import spark.Request;

public class CreateGameHandler {

    private String result;
    private int statusCode;

    public CreateGameHandler(Request req, GameService service) {
        CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        createGameRequest = new CreateGameRequest(req.headers("Authorization"), createGameRequest.gameName());

        if (createGameRequest.authToken() == null || createGameRequest.gameName() == null) {
            ErrorMessage error = new ErrorMessage("Error: bad request");
            result = new Gson().toJson(error);
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
