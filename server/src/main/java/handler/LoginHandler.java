package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.*;
import spark.Request;
import spark.Response;

public class LoginHandler {

    private String result;
    private int statusCode;

    public LoginHandler(Request req, UserService service) {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);

//        if (loginRequest.username() == null || loginRequest.password() == null) {
//        ErrorMessage error = new ErrorMessage("Error: bad request");
//        result = new Gson().toJson(error);
//            statusCode = 401;
//        } else {
        try {
            LoginResult loginResult = service.login(loginRequest);
            result = new Gson().toJson(loginResult);
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
