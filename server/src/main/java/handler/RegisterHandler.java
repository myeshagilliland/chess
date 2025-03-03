package handler;

import dataaccess.DataAccessException;
import service.ErrorMessage;
import service.RegisterRequest;
import com.google.gson.Gson;
import service.RegisterResult;
import service.UserService;
import spark.*;

public class RegisterHandler {

    private String result;
    private int statusCode;

    public RegisterHandler (Request req, UserService service) {
        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);

        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            ErrorMessage error = new ErrorMessage("Error: bad request");
            result = new Gson().toJson(error);
            statusCode = 400;
        } else {
            try {
                RegisterResult registerResult = service.register(registerRequest);
                result = new Gson().toJson(registerResult);
                statusCode = 200;
            } catch (DataAccessException e) {
                ErrorMessage error = new ErrorMessage(e.getMessage());
                result = new Gson().toJson(error);
                statusCode = 403;
            }
        }
    }

    public int getStatusCode() {return statusCode;}

    public String getResult() {return result;}

}
