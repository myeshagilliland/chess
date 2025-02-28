package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class GameService {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
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

    private static int generateGameID() {
        return new Random().nextInt(10000);
    }


    public CreateGameResult createGame(CreateGameRequest req) throws DataAccessException {

        if (authDAO.findAuth(req.authToken()) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        int gameID = generateGameID();
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(gameID, null, null, req.gameName(), chessGame);
        gameDAO.createGame(gameData);

        return new CreateGameResult(gameID);
    }

}
//    public LoginResult login(LoginRequest req) throws DataAccessException {
//
//        UserData user = userDAO.findUser(req.username());
//
//        if (user == null || !Objects.equals(req.password(), user.password())) {
//            throw new DataAccessException("Error: unauthorized");
//        }
//
//        String authToken = authenticate(req.username());
//
//        return new LoginResult(req.username(), authToken);
//    }
//
//    public LogoutResult logout(LogoutRequest req) throws DataAccessException {
//
//        if (authDAO.findAuth(req.authToken()) == null) {
//            throw new DataAccessException("Error: unauthorized");
//        }
//
//        authDAO.deleteAuth(req.authToken());
//
//        return new LogoutResult();
//    }

