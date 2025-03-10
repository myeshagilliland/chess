package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requestresult.ClearRequest;
import requestresult.ClearResult;
import requestresult.RegisterRequest;
import requestresult.RegisterResult;
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
//
//    private String result;
//    private int statusCode;
//
//    public ClearHandler(Request req, UserService service) {
//        ClearRequest clearRequest = new ClearRequest();
//
//        try {
//            ClearResult clearResult = service.clear(clearRequest);
//            result = new Gson().toJson(clearResult);
//            statusCode = 200;
//        } catch (DataAccessException e) {
//            ErrorMessage error = new ErrorMessage(e.getMessage());
//            result = new Gson().toJson(error);
//            statusCode = 500;
//        }
//    }
//
//    public int getStatusCode() {
//        return statusCode;
//    }
//
//    public String getResult() {
//        return result;
//    }
//
//}
