package service;

import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.Test;
import requestresult.*;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private RegisterResult getRegisterResult() {
        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDao(), new MemoryGameDAO());
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult registerResult = null;
        try {
            registerResult = userService.register(registerRequest);
        } catch (ServiceException e) {}

        return registerResult;
    }


    @Test
    public void testCreateGamePositive() {
        //given
        String authToken = getRegisterResult().authToken();
        CreateGameRequest request = new CreateGameRequest(authToken, "gameName");

        //expected

        //when
        CreateGameResult answer;
        try {
            GameDAO gameDAO = new MemoryGameDAO();
            GameService gameService = new GameService(new MemoryUserDAO(), new MemoryAuthDao(), gameDAO);
            answer = gameService.createGame(request);

        //then
            assertTrue(answer.gameID() > 0);
        } catch (ServiceException e) {}

    }

    @Test
    public void testCreateGameNegative() {
        //given
        String authToken = getRegisterResult().authToken();
        CreateGameRequest request = new CreateGameRequest(authToken, "gameName");

        //expected
        UnauthorizedException expected = new UnauthorizedException();

        //when
        try {
            GameService gameService = new GameService(new MemoryUserDAO(), new MemoryAuthDao(), new MemoryGameDAO());
            gameService.createGame(request);

        } catch (ServiceException e) {

        //then
            assertEquals(e.getMessage(), expected.getMessage());
        }

    }

    public RegisterResult generateUserInfo (UserService userService) {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult userInfo = null;
        try {
            userInfo = userService.register(registerRequest);
        } catch (ServiceException e) {}
        return userInfo;
    }

    public CreateGameResult generateCreateGameResult (RegisterResult userInfo, GameService gameService) {
        String authToken = userInfo.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "gameName");
        CreateGameResult createGameResult = new CreateGameResult(0);
        try {
            createGameResult = gameService.createGame(createGameRequest);
        } catch (ServiceException e) {
            System.out.println("failed to create game");
        }
        return createGameResult;
    }

    @Test
    public void joinGamePositive() {
        //given
        UserDAO userDao = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDao();
        GameDAO gameDAO = new MemoryGameDAO();

        UserService userService = new UserService(userDao, authDAO, gameDAO);
        GameService gameService = new GameService(userDao, authDAO, gameDAO);
        RegisterResult userInfo = generateUserInfo(userService);
        CreateGameResult createGameResult = generateCreateGameResult(userInfo, gameService);

        JoinGameRequest joinGameRequest = new JoinGameRequest(userInfo.authToken(), "WHITE", createGameResult.gameID());

        //expected

        //when
        try {
            gameService.joinGame(joinGameRequest);
        } catch (ServiceException e) {
            System.out.println("failed to join game");
        }

        //then
        try {
            assert Objects.equals(gameDAO.findGame(createGameResult.gameID()).whiteUsername(), userInfo.username());
        } catch (DataAccessException e) {}

    }

    @Test
    public void joinGameNegative() {
        //given
        UserDAO userDao = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDao();
        GameDAO gameDAO = new MemoryGameDAO();

        UserService userService = new UserService(userDao, authDAO, gameDAO);
        GameService gameService = new GameService(userDao, authDAO, gameDAO);
        RegisterResult userInfo = generateUserInfo(userService);
        CreateGameResult createGameResult = generateCreateGameResult(userInfo, gameService);

        JoinGameRequest joinGameRequest = new JoinGameRequest(userInfo.authToken(), "WHITE", createGameResult.gameID());

        //expected
        AlreadyTakenException expected = new AlreadyTakenException();

        //when
        try {
            gameService.joinGame(joinGameRequest);
            gameService.joinGame(joinGameRequest);
        } catch (ServiceException e) {

        //then
            assert Objects.equals(e.getMessage(), expected.getMessage());
        }
    }

    @Test
    public void listGamesPositive() {
        //given
        UserDAO userDao = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDao();
        GameDAO gameDAO = new MemoryGameDAO();

        UserService userService = new UserService(userDao, authDAO, gameDAO);
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult userInfo = null;
        try {
            userInfo = userService.register(registerRequest);
        } catch (ServiceException e) {}

        String authToken = userInfo.authToken();
        CreateGameRequest createGameRequest1 = new CreateGameRequest(authToken, "gameName1");
        GameService gameService = new GameService(userDao, authDAO, gameDAO);
        CreateGameResult createGameResult1 = new CreateGameResult(0);
        try {
            createGameResult1 = gameService.createGame(createGameRequest1);
        } catch (ServiceException e) {
            System.out.println("failed to create game");
        }

        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);

        //expected
        GameData gameData1 = null;
        try {
            gameData1 = gameDAO.findGame(createGameResult1.gameID());
        } catch (DataAccessException e) {}
        ArrayList<GameData> games = new ArrayList<>();
        games.add(gameData1);
        ListGamesResult expected = new ListGamesResult(games);

        //when
        ListGamesResult listGamesResult;
        try {
            listGamesResult = gameService.listGames(listGamesRequest);

        //then
            assertEquals(listGamesResult, expected);
        } catch (ServiceException e) {}

    }

    @Test
    public void listGamesNegative() {
        //given
        UserDAO userDao = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDao();
        GameDAO gameDAO = new MemoryGameDAO();

        UserService userService = new UserService(userDao, authDAO, gameDAO);
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult userInfo = null;
        try {
            userInfo = userService.register(registerRequest);
        } catch (ServiceException e) {}

        String authToken = userInfo.authToken();
        CreateGameRequest createGameRequest1 = new CreateGameRequest(authToken, "gameName1");
        CreateGameRequest createGameRequest2 = new CreateGameRequest(authToken, "gameName2");
        GameService gameService = new GameService(userDao, authDAO, gameDAO);
        CreateGameResult createGameResult1 = new CreateGameResult(0);
        CreateGameResult createGameResult2 = new CreateGameResult(0);
        try {
            createGameResult1 = gameService.createGame(createGameRequest1);
            createGameResult2 = gameService.createGame(createGameRequest1);
        } catch (ServiceException e) {
            System.out.println("failed to create game");
        }

        ListGamesRequest listGamesRequest = new ListGamesRequest("authToken");

        //expected
        GameData gameData1 = null;
        GameData gameData2 = null;
        try {
            gameData1 = gameDAO.findGame(createGameResult1.gameID());
            gameData2 = gameDAO.findGame(createGameResult2.gameID());
        } catch (DataAccessException e) {}
        GameData[] games = {gameData1, gameData2};

        UnauthorizedException expected = new UnauthorizedException();


        //when
        ListGamesResult listGamesResult;
        try {
            listGamesResult = gameService.listGames(listGamesRequest);
        } catch (ServiceException e) {

        //then
            assert Objects.equals(e.getMessage(), expected.getMessage());
        }

    }
}