package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public class GameService {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

//    private static String generateToken() {
//        return UUID.randomUUID().toString();
//    }

//    private String authenticate(String username) {
//        String authToken = generateToken();
//        AuthData authData = new AuthData(authToken, username);
//        authDAO.createAuth(authData);
//        return authToken;
//    }

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

    public JoinGameResult joinGame(JoinGameRequest req) throws DataAccessException {

        AuthData authData = authDAO.findAuth(req.authToken());
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        GameData gameData = gameDAO.findGame(req.gameID());
        if (gameData == null) {
            throw new DataAccessException("Error: game does not exist");
        }

        boolean whiteTaken = (Objects.equals(req.playerColor(), "WHITE") & gameData.whiteUsername() != null);
        boolean blackTaken = (Objects.equals(req.playerColor(), "BLACK") & gameData.blackUsername() != null);
        if (whiteTaken || blackTaken) {
            throw new DataAccessException("Error: already taken");
        }

        GameData updatedGameData;
        if (Objects.equals(req.playerColor(), "WHITE")) {
            updatedGameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.game());
        } else {
            updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.game());
        }

        gameDAO.updateGame(updatedGameData);

        return new JoinGameResult();
    }

    public ListGamesResult listGames(ListGamesRequest req) throws DataAccessException {

        AuthData authData = authDAO.findAuth(req.authToken());
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        Collection<GameData> gamesCollection = gameDAO.listGames();
        ArrayList<GameData> games = new ArrayList<>(gamesCollection);

        return new ListGamesResult(games);
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

