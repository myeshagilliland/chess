package handler;

import com.google.gson.Gson;
import requestresult.LogoutRequest;
import requestresult.LogoutResult;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) {
        LogoutRequest logoutRequest = new LogoutRequest(req.headers("Authorization"));

        try {
            LogoutResult logoutResult = userService.logout(logoutRequest);
            res.status(200);
            return new Gson().toJson(logoutResult);
        } catch (ServiceException e) {
            res.status(e.getStatusCode());
            return e.getErrorMessage();
        }
    }
}