package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    private String authenticate(String username) {
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, username);
        authDAO.createAuth(authData);
        return authToken;
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException {

        if (userDAO.findUser(req.username()) != null) {
            throw new DataAccessException("Error: already taken");
        }

        UserData userData = new UserData(req.username(), req.password(), req.email());
        userDAO.createUser(userData);

        String authToken = authenticate(req.username());

        return new RegisterResult(req.username(), authToken);
    }

    public LoginResult login(LoginRequest req) throws DataAccessException {

        if (userDAO.findUser(req.username()) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        String authToken = authenticate(req.username());

        return new LoginResult(req.username(), authToken);
    }

    public void logout(LogoutRequest req) throws DataAccessException {

        if (authDAO.findAuth(req.authToken()) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        authDAO.deleteAuth(req.authToken());
    }

}
