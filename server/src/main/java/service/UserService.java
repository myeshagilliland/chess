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

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException {

        if (userDAO.findUser(req.username()) != null) {
            throw new DataAccessException("Error: already taken");
        }

        UserData userData = new UserData(req.username(), req.password(), req.email());
        userDAO.createUser(userData);
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, req.username());
        authDAO.createAuth(authData);

        return new RegisterResult(req.username(), authToken);
    }

//    public LoginResult login(LoginRequest loginRequest) {}
//    public void logout(LogoutRequest logoutRequest) {}


}
