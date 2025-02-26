package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {

    private MemoryUserDAO userDAO = new MemoryUserDAO();
    private MemoryAuthDao authDAO = new MemoryAuthDao();
    private MemoryGameDAO gameDAO = new MemoryGameDAO();

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException {
        UserData userData = new UserData(req.username(), req.password(), req.email());

        if (userDAO.findUser(req.username()) == null) {
            throw new DataAccessException("Error: already taken");
        }

        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, req.username());
        authDAO.createAuth(authData);

        return new RegisterResult(req.username(), authToken);
    }

//    public LoginResult login(LoginRequest loginRequest) {}
//    public void logout(LogoutRequest logoutRequest) {}


}
