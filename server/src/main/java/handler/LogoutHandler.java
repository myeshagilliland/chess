package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.LoginRequest;
import service.LoginResult;
import service.LogoutRequest;
import service.UserService;
import spark.Request;
import spark.Response;

public class LogoutHandler {

    private String result;
    private int statusCode;

    public LogoutHandler(Request req, Response res, UserService service) {
        LogoutRequest logoutRequest = new Gson().fromJson(req.body(), LogoutRequest.class);

//        if (loginRequest.username() == null || loginRequest.password() == null) {
//            result = new Gson().toJson("Error: bad request");
//            statusCode = 401;
//        } else {
        try {
            service.logout(logoutRequest);
            result = null;
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
