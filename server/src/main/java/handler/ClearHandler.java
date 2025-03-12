package handler;

import com.google.gson.Gson;
import requestresult.ClearRequest;
import requestresult.ClearResult;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {
    private final UserService userService;

    public ClearHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) {
        ClearRequest clearRequest = new ClearRequest();

        try {
            ClearResult clearResult = userService.clear(clearRequest);
            res.status(200);
            return new Gson().toJson(clearResult);
        } catch (ServiceException e) {
            res.status(e.getStatusCode());
            return e.getErrorMessage();
        }
    }
}