package handler;

import dataaccess.DataAccessException;
import service.ErrorMessage;
import service.RegisterRequest;
import com.google.gson.Gson;
import service.RegisterResult;
import service.UserService;
import spark.*;

public class RegisterHandler {

    private String result = null;
    private int statusCode = 0;
//    private String errorMessage = "";

    public RegisterHandler (Request req, Response res, UserService service) {
        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
//        System.out.println(registerRequest.toString());
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            ErrorMessage error = new ErrorMessage("Error: bad request");
            result = new Gson().toJson(error);
//            result = new Gson().toJson("Error: bad request");
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
//        if (statusCode == 0) {
//            result = "Error: unidentified";
//            statusCode = 500;
//        }
    }

    public int getStatusCode() {return statusCode;}

    public String getResult() {return result;}

    // JSON -> Java request object
    // call Service class on req obj
    // Java response obj -> JSON
    // return

    // OR catch error -> return JSON error message

    //    var pet = new Gson().fromJson(req.body(), RegisterHandler.class);
}
