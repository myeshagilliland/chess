package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.*;
import spark.Request;
import spark.Response;

public class LoginHandler {

    private String result;
    private int statusCode;

    public LoginHandler(Request req, Response res, UserService service) {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);

//        if (loginRequest.username() == null || loginRequest.password() == null) {
//            result = new Gson().toJson("Error: bad request");
//            statusCode = 401;
//        } else {
        try {
            LoginResult loginResult = service.login(loginRequest);
            result = new Gson().toJson(loginResult);
            statusCode = 200;
        } catch (DataAccessException e) {
            result = new Gson().toJson(e.getMessage());
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
