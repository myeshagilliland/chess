package handler;

import com.google.gson.Gson;
import exception.ServiceException;
import requestresult.LoginRequest;
import requestresult.LoginResult;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);

        try {
            LoginResult loginResult = userService.login(loginRequest);
            res.status(200);
            return new Gson().toJson(loginResult);
        } catch (ServiceException e) {
            res.status(e.getStatusCode());
            return e.getErrorMessage();
        }
    }
}