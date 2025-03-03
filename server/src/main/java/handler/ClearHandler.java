package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requestresult.ClearRequest;
import requestresult.ClearResult;
import service.*;
import spark.Request;

public class ClearHandler {

    private String result;
    private int statusCode;

    public ClearHandler(Request req, UserService service) {
        ClearRequest clearRequest = new ClearRequest();

        try {
            ClearResult clearResult = service.clear(clearRequest);
            result = new Gson().toJson(clearResult);
            statusCode = 200;
        } catch (DataAccessException e) {
            ErrorMessage error = new ErrorMessage(e.getMessage());
            result = new Gson().toJson(error);
            statusCode = 500;
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResult() {
        return result;
    }

}
