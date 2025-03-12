package service;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import requestresult.*;

import java.util.Objects;
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

    private String authenticate(String username) throws DataAccessException {
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, username);
        authDAO.createAuth(authData);
        return authToken;
    }

    public RegisterResult register(RegisterRequest req) throws ServiceException {

        try {

            if (req.username() == null || req.password() == null || req.email() == null) {
                throw new BadRequestException();
            }

            if (userDAO.findUser(req.username()) != null) {
//                throw new DataAccessException("Error: already taken");
                throw new AlreadyTakenException();
            }

            UserData userData = new UserData(req.username(), req.password(), req.email());
            userDAO.createUser(userData);

            String authToken = authenticate(req.username());

            return new RegisterResult(req.username(), authToken);
        } catch (DataAccessException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public LoginResult login(LoginRequest req) throws ServiceException {

        try {
            UserData user = userDAO.findUser(req.username());

//            if (user == null || !Objects.equals(req.password(), user.password())) {
            if (user == null || !BCrypt.checkpw(req.password(), user.password())) {
//                throw new DataAccessException("Error: unauthorized");
                throw new UnauthorizedException();
            }

            String authToken = authenticate(req.username());

            return new LoginResult(req.username(), authToken);
        } catch (DataAccessException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public LogoutResult logout(LogoutRequest req) throws ServiceException {

        try {

            if (authDAO.findAuth(req.authToken()) == null) {
//                throw new DataAccessException("Error: unauthorized");
                throw new UnauthorizedException();
            }

            authDAO.deleteAuth(req.authToken());

            return new LogoutResult();
        } catch (DataAccessException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public ClearResult clear(ClearRequest req) throws ServiceException {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();

            return new ClearResult();
        } catch (DataAccessException e) {
            throw new ServiceException(e.getMessage());
        }
    }

}
