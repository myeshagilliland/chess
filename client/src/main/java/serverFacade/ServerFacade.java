package serverFacade;

import com.google.gson.Gson;
import exception.ServiceException;
import model.AuthData;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {

    static int port;

    public ServerFacade(int port) {
        this.port = port;
    }

    public AuthData register(String username, String password, String email) throws ServiceException {
        String path = "/user";
        UserData user = new UserData(username, password, email);
        return this.makeRequest("POST", path, user, AuthData.class);
    }


    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ServiceException {
        try {
            String serverUrl = String.format("http://localhost:%d", port);
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
//            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        }
//        } catch (ServiceException ex) {
//            throw ex;
//        }
        catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

//    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ServiceException {
//        var status = http.getResponseCode();
//        if (!isSuccessful(status)) {
//            try (InputStream respErr = http.getErrorStream()) {
//                if (respErr != null) {
//                    throw ServiceException.fromJson(respErr);
//                }
//            }
//
//            throw new ServiceException("other failure: " + status);
//        }
//    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


//    private boolean isSuccessful(int status) {
//        return status / 100 == 2;
//    }

}
