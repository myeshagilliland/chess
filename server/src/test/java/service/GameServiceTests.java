//package service;
//
//import dataaccess.*;
//import model.GameData;
//import org.junit.jupiter.api.Test;
//import requestresult.*;
//
//import java.util.ArrayList;
//import java.util.Objects;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class GameServiceTests {
//
//    private RegisterResult getRegisterResult() {
//        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDao(), new MemoryGameDAO());
//        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
//        RegisterResult registerResult = null;
//        try {
//            registerResult = userService.register(registerRequest);
//        } catch (DataAccessException e) {}
//
//        return registerResult;
//    }
//
//
//    @Test
//    public void testCreateGamePositive() {
//        //given
//        String authToken = getRegisterResult().authToken();
//        CreateGameRequest request = new CreateGameRequest(authToken, "gameName");
//
//        //expected
//
//        //when
//        CreateGameResult answer;
//        try {
//            GameDAO gameDAO = new MemoryGameDAO();
//            GameService gameService = new GameService(new MemoryUserDAO(), new MemoryAuthDao(), gameDAO);
//            answer = gameService.createGame(request);
//
//        //then
//            assertTrue(answer.gameID() > 0);
//        } catch (DataAccessException e) {}
//
//    }
//
//    @Test
//    public void testCreateGameNegative() {
//        //given
//        String authToken = getRegisterResult().authToken();
//        CreateGameRequest request = new CreateGameRequest(authToken, "gameName");
//
//        //expected
//        DataAccessException expected = new DataAccessException("Error: unauthorized");
//
//        //when
//        try {
//            GameService gameService = new GameService(new MemoryUserDAO(), new MemoryAuthDao(), new MemoryGameDAO());
//            gameService.createGame(request);
//
//        } catch (DataAccessException e) {
//
//        //then
//            assertEquals(e.getMessage(), expected.getMessage());
//        }
//
//    }
//
//    public RegisterResult generateUserInfo (UserService userService) {
//        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
//        RegisterResult userInfo = null;
//        try {
//            userInfo = userService.register(registerRequest);
//        } catch (DataAccessException e) {}
//        return userInfo;
//    }
//
//    public CreateGameResult generateCreateGameResult (RegisterResult userInfo, GameService gameService) {
//        String authToken = userInfo.authToken();
//        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "gameName");
//        CreateGameResult createGameResult = new CreateGameResult(0);
//        try {
//            createGameResult = gameService.createGame(createGameRequest);
//        } catch (DataAccessException e) {
//            System.out.println("failed to create game");
//        }
//        return createGameResult;
//    }
//
//    @Test
//    public void joinGamePositive() {
//        //given
//        UserDAO userDao = new MemoryUserDAO();
//        AuthDAO authDAO = new MemoryAuthDao();
//        GameDAO gameDAO = new MemoryGameDAO();
//
//        UserService userService = new UserService(userDao, authDAO, gameDAO);
//        GameService gameService = new GameService(userDao, authDAO, gameDAO);
//        RegisterResult userInfo = generateUserInfo(userService);
//        CreateGameResult createGameResult = generateCreateGameResult(userInfo, gameService);
//
//        JoinGameRequest joinGameRequest = new JoinGameRequest(userInfo.authToken(), "WHITE", createGameResult.gameID());
//
//        //expected
//
//        //when
//        try {
//            gameService.joinGame(joinGameRequest);
//        } catch (DataAccessException e) {
//            System.out.println("failed to join game");
//        }
//
//        //then
//        assert Objects.equals(gameDAO.findGame(createGameResult.gameID()).whiteUsername(), userInfo.username());
//
//    }
//
//    @Test
//    public void joinGameNegative() {
//        //given
//        UserDAO userDao = new MemoryUserDAO();
//        AuthDAO authDAO = new MemoryAuthDao();
//        GameDAO gameDAO = new MemoryGameDAO();
//
//        UserService userService = new UserService(userDao, authDAO, gameDAO);
//        GameService gameService = new GameService(userDao, authDAO, gameDAO);
//        RegisterResult userInfo = generateUserInfo(userService);
//        CreateGameResult createGameResult = generateCreateGameResult(userInfo, gameService);
//
//        JoinGameRequest joinGameRequest = new JoinGameRequest(userInfo.authToken(), "WHITE", createGameResult.gameID());
//
//        //expected
//        DataAccessException expected = new DataAccessException("Error: already taken");
//
//        //when
//        try {
//            gameService.joinGame(joinGameRequest);
//            gameService.joinGame(joinGameRequest);
//        } catch (DataAccessException e) {
//
//        //then
//            assert Objects.equals(e.getMessage(), expected.getMessage());
//        }
//    }
//
//    @Test
//    public void listGamesPositive() {
//        //given
//        UserDAO userDao = new MemoryUserDAO();
//        AuthDAO authDAO = new MemoryAuthDao();
//        GameDAO gameDAO = new MemoryGameDAO();
//
//        UserService userService = new UserService(userDao, authDAO, gameDAO);
//        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
//        RegisterResult userInfo = null;
//        try {
//            userInfo = userService.register(registerRequest);
//        } catch (DataAccessException e) {}
//
//        String authToken = userInfo.authToken();
//        CreateGameRequest createGameRequest1 = new CreateGameRequest(authToken, "gameName1");
//        GameService gameService = new GameService(userDao, authDAO, gameDAO);
//        CreateGameResult createGameResult1 = new CreateGameResult(0);
//        try {
//            createGameResult1 = gameService.createGame(createGameRequest1);
//        } catch (DataAccessException e) {
//            System.out.println("failed to create game");
//        }
//
//        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
//
//        //expected
//        GameData gameData1 = gameDAO.findGame(createGameResult1.gameID());
//        ArrayList<GameData> games = new ArrayList<>();
//        games.add(gameData1);
//        ListGamesResult expected = new ListGamesResult(games);
//
//        //when
//        ListGamesResult listGamesResult;
//        try {
//            listGamesResult = gameService.listGames(listGamesRequest);
//
//        //then
//            assertEquals(listGamesResult, expected);
//        } catch (DataAccessException e) {}
//
//    }
//
//    @Test
//    public void listGamesNegative() {
//        //given
//        UserDAO userDao = new MemoryUserDAO();
//        AuthDAO authDAO = new MemoryAuthDao();
//        GameDAO gameDAO = new MemoryGameDAO();
//
//        UserService userService = new UserService(userDao, authDAO, gameDAO);
//        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
//        RegisterResult userInfo = null;
//        try {
//            userInfo = userService.register(registerRequest);
//        } catch (DataAccessException e) {}
//
//        String authToken = userInfo.authToken();
//        CreateGameRequest createGameRequest1 = new CreateGameRequest(authToken, "gameName1");
//        CreateGameRequest createGameRequest2 = new CreateGameRequest(authToken, "gameName2");
//        GameService gameService = new GameService(userDao, authDAO, gameDAO);
//        CreateGameResult createGameResult1 = new CreateGameResult(0);
//        CreateGameResult createGameResult2 = new CreateGameResult(0);
//        try {
//            createGameResult1 = gameService.createGame(createGameRequest1);
//            createGameResult2 = gameService.createGame(createGameRequest1);
//        } catch (DataAccessException e) {
//            System.out.println("failed to create game");
//        }
//
//        ListGamesRequest listGamesRequest = new ListGamesRequest("authToken");
//
//        //expected
//        GameData gameData1 = gameDAO.findGame(createGameResult1.gameID());
//        GameData gameData2 = gameDAO.findGame(createGameResult2.gameID());
//        GameData[] games = {gameData1, gameData2};
//
//        DataAccessException expected = new DataAccessException("Error: unauthorized");
//
//
//        //when
//        ListGamesResult listGamesResult;
//        try {
//            listGamesResult = gameService.listGames(listGamesRequest);
//        } catch (DataAccessException e) {
//
//        //then
//            assert Objects.equals(e.getMessage(), expected.getMessage());
//        }
//
//    }
//}