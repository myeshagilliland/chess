package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import requestresult.*;
import java.util.*;

public class GameService {

    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    private static int generateGameID() {
        return new Random().nextInt(10000);
    }

    public CreateGameResult createGame(CreateGameRequest req) throws ServiceException {

        try {

            if (req.authToken() == null || req.gameName() == null) {
                throw new BadRequestException();
            }

            if (authDAO.findAuth(req.authToken()) == null) {
                throw new UnauthorizedException();
            }

            int gameID = generateGameID();
            ChessGame chessGame = new ChessGame();
            GameData gameData = new GameData(gameID, null, null, req.gameName(), chessGame);
            gameDAO.createGame(gameData);

            return new CreateGameResult(gameID);
        } catch (DataAccessException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public JoinGameResult joinGame(JoinGameRequest req) throws ServiceException {

        try {

            if (req.authToken() == null || (!Objects.equals(req.playerColor(), "WHITE")
                    && !Objects.equals(req.playerColor(), "BLACK"))  || req.gameID() == 0) {
                throw new BadRequestException();
            }

            AuthData authData = authDAO.findAuth(req.authToken());
            if (authData == null) {
                throw new UnauthorizedException();
            }

            GameData gameData = gameDAO.findGame(req.gameID());

            boolean whiteTaken = (Objects.equals(req.playerColor(), "WHITE") & gameData.whiteUsername() != null);
            boolean blackTaken = (Objects.equals(req.playerColor(), "BLACK") & gameData.blackUsername() != null);
            if (whiteTaken || blackTaken) {
                throw new AlreadyTakenException();
            }

            GameData updatedGameData;
            if (Objects.equals(req.playerColor(), "WHITE")) {
                updatedGameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(),
                        gameData.gameName(), gameData.chessGame());
            } else {
                updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(),
                        gameData.gameName(), gameData.chessGame());
            }

            gameDAO.updateGame(updatedGameData);

            return new JoinGameResult();
        } catch (DataAccessException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public ListGamesResult listGames(ListGamesRequest req) throws ServiceException {

        try {
            AuthData authData = authDAO.findAuth(req.authToken());
            if (authData == null) {
                throw new UnauthorizedException();
            }

            Collection<GameData> gamesCollection = gameDAO.listGames();
            ArrayList<GameData> games = new ArrayList<>(gamesCollection);

            return new ListGamesResult(games);
        } catch (DataAccessException e) {
            throw new ServiceException(e.getMessage());
        }
    }

}


