package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.*;
import spark.Request;
import spark.Response;

public class LogoutHandler {

    private String result;
    private int statusCode;

    public LogoutHandler(Request req, UserService service) {
        LogoutRequest logoutRequest = new LogoutRequest(req.headers("Authorization"));
        System.out.println(logoutRequest);

        try {
            LogoutResult logoutResult = service.logout(logoutRequest);
            result = new Gson().toJson(logoutResult); //error here
            System.out.println(result);
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
