package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requestresult.JoinGameRequest;
import requestresult.JoinGameResult;
import service.*;
import spark.Request;
import java.util.Objects;

public class JoinGameHandler {

    private String result;
    private int statusCode;

    public JoinGameHandler(Request req, GameService service) {
        JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
        joinGameRequest = new JoinGameRequest(req.headers("Authorization"),
                joinGameRequest.playerColor(), joinGameRequest.gameID());

        if (req.headers("Authorization") == null || (!Objects.equals(joinGameRequest.playerColor(), "WHITE")
                && !Objects.equals(joinGameRequest.playerColor(), "BLACK"))  || joinGameRequest.gameID() == 0) {
            ErrorMessage error = new ErrorMessage("Error: bad request");
            result = new Gson().toJson(error);
            statusCode = 400;
        } else {
            try {
                JoinGameResult joinGameResult = service.joinGame(joinGameRequest);
                result = new Gson().toJson(joinGameResult);
                statusCode = 200;
            } catch (DataAccessException e) {
                ErrorMessage error = new ErrorMessage(e.getMessage());
                result = new Gson().toJson(error);
                if (Objects.equals(error.message(), "Error: unauthorized")) {
                    statusCode = 401;
                } else if (Objects.equals(error.message(), "Error: already taken")) {
                    statusCode = 403;
                } else {
                    statusCode = 500;
                }
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
