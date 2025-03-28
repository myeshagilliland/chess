package facade;

import com.google.gson.Gson;
import exception.ServiceException;
import requestresult.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

public class ServerFacade {

    static int port;

    public ServerFacade(int port) {
        this.port = port;
    }

    public RegisterResult register(String username, String password, String email) throws ServiceException {
        String path = "/user";
        RegisterRequest registerData = new RegisterRequest(username, password, email);
        return this.makeRequest("POST", path, null, registerData, RegisterResult.class);
    }

    public LoginResult login(String username, String password) throws ServiceException {
        String path = "/session";
        LoginRequest loginData = new LoginRequest(username, password);
        return this.makeRequest("POST", path, null, loginData, LoginResult.class);
    }

    public void logout(String authToken) throws ServiceException {
        String path = "/session";
        this.makeRequest("DELETE", path, authToken, null, null);
    }

    public CreateGameResult createGame(String authToken, String gameName) throws ServiceException {
        String path = "/game";
        CreateGameRequest createData = new CreateGameRequest(authToken, gameName);
        return this.makeRequest("POST", path, authToken, createData, CreateGameResult.class);
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws ServiceException {
        String path = "/game";
        JoinGameRequest joinData = new JoinGameRequest(authToken, playerColor, gameID);
        this.makeRequest("PUT", path, authToken, joinData, null);
    }

    public ListGamesResult listGames(String authToken) throws ServiceException {
        String path = "/game";
        var gamesList = this.makeRequest("GET", path, authToken, null, ListGamesResult.class);
        return gamesList;
    }

    public void clear() throws ServiceException {
        String path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    private <T> T makeRequest(String method, String path, String auth, Object request, Class<T> responseClass)
            throws ServiceException {
        try {
            String serverUrl = String.format("http://localhost:%d", port);
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, auth, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }


    private static void writeBody(Object request, String auth, HttpURLConnection http) throws IOException {
        if (auth != null) {
            http.addRequestProperty("Authorization", auth);
        }
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ServiceException {
        var status = http.getResponseCode();
        if (status != 200) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    var errorMap = new Gson().fromJson(new InputStreamReader(respErr), HashMap.class);
                    String errorMessage = errorMap.get("message").toString();
                    throw new ServiceException(errorMessage);
                }
            }
            throw new ServiceException("Unexpected error: " + status);
        }
    }

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

}
