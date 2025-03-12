package handler;

import requestresult.RegisterRequest;
import com.google.gson.Gson;
import requestresult.RegisterResult;
import service.ServiceException;
import service.UserService;
import spark.*;

public class RegisterHandler implements Route {

    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) {
        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);

        try {
            RegisterResult registerResult = userService.register(registerRequest);
            res.status(200);
            return new Gson().toJson(registerResult);
        } catch (ServiceException e) {
            res.status(e.getStatusCode());
            return e.getErrorMessage();
        }
    }
}