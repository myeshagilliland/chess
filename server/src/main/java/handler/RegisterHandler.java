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

//    private String result;
//    private int statusCode;
//
//    public RegisterHandler (Request req, UserService service) {
//        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
//
//        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
//            ErrorMessage error = new ErrorMessage("Error: bad request");
//            result = new Gson().toJson(error);
//            statusCode = 400;
//        } else {
//            try {
//                RegisterResult registerResult = service.register(registerRequest);
//                result = new Gson().toJson(registerResult);
//                statusCode = 200;
//            } catch (DataAccessException e) {
//                ErrorMessage error = new ErrorMessage(e.getMessage());
//                result = new Gson().toJson(error);
//                statusCode = 403;
//            }
//        }
//    }
//
//    public int getStatusCode() {return statusCode;}
//
//    public String getResult() {return result;}
//
//}
