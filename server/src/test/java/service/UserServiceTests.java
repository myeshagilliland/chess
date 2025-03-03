package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    @Test
    public void testRegisterPositive() {
        //given
        RegisterRequest request = new RegisterRequest("name", "pwd", "me@mail");

        //expected
        RegisterResult expected = new RegisterResult("name", "authToken");

        //when
        RegisterResult answer;
        try {
            UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDao(), new MemoryGameDAO());
            answer = userService.register(request);

        //then
            assertEquals(expected.username(), answer.username());
            assertNotNull(answer.authToken());
        } catch (DataAccessException e) {}

    }

    @Test
    public void testRegisterNegative() {
        //given
        RegisterRequest req = new RegisterRequest("name", "pwd", "me@mail");

        //expected
        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDao(), new MemoryGameDAO());
        try {
            userService.register(req);
        } catch (DataAccessException e) {
            System.out.println("Failed to register first request"); //this is executing, why?
        }
        DataAccessException expected = new DataAccessException("Error: already taken");

        //when
        try {
            userService.register(req);
        } catch (DataAccessException e) {

        //then
            assertEquals(e.getMessage(), expected.getMessage());
        }

    }

    @Test
    public void testLoginPositive() {
        //given
        LoginRequest request = new LoginRequest("name", "pwd");

        //expected
        LoginResult expected = new LoginResult("name", "authToken");

        //when
        LoginResult answer;
        try {
            UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDao(), new MemoryGameDAO());
            answer = userService.login(request);

        //then
            assertEquals(expected.username(), answer.username());
            assertNotNull(answer.authToken());
        } catch (DataAccessException e) {}

    }

    @Test
    public void testLoginNegative() {
        //given
        LoginRequest req = new LoginRequest("name", "pwd");

        //expected
        DataAccessException expected = new DataAccessException("Error: unauthorized");

        //when
        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDao(), new MemoryGameDAO());
        try {
            userService.login(req);
        } catch (DataAccessException e) {

        //then
            assertEquals(e.getMessage(), expected.getMessage());
        }

    }

    @Test
    public void testLogoutPositive() {
        //given
        LogoutRequest request = new LogoutRequest("authToken");

        //expected

        //when
        try {
            AuthDAO authDAO = new MemoryAuthDao();
            UserService userService = new UserService(new MemoryUserDAO(), authDAO, new MemoryGameDAO());
            userService.logout(request);

        //then
            assert(authDAO.findAuth("authToken") == null);
        } catch (DataAccessException e) {}
    }

    @Test
    public void testLogoutNegative() {
        //given
        LogoutRequest request = new LogoutRequest("authToken");

        //expected
        DataAccessException expected = new DataAccessException("Error: unauthorized");

        //when
        try {
            UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDao(), new MemoryGameDAO());
            userService.logout(request);
        } catch (DataAccessException e) {

        //then
            assertEquals(e.getMessage(), expected.getMessage());
        }
    }

    @Test
    public void testClearPositive() {
        //given
        UserDAO userDao = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDao();
        GameDAO gameDAO = new MemoryGameDAO();

        UserService userService = new UserService(userDao, authDAO, gameDAO);
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult userInfo = null;
        try {
            userInfo = userService.register(registerRequest);
        } catch (DataAccessException e) {}

        String authToken = userInfo.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "gameName1");
        GameService gameService = new GameService(userDao, authDAO, gameDAO);
//        CreateGameResult createGameResult = new CreateGameResult(0);
        try {
//            createGameResult = gameService.createGame(createGameRequest);
            gameService.createGame(createGameRequest);
        } catch (DataAccessException e) {}

        ClearRequest clearRequest = new ClearRequest();

        //expected
//        HashMap<String, AuthData> expectedAuth = new HashMap<String, AuthData>();
//        HashMap<Integer, GameData> expectedGame = new HashMap<Integer, GameData>();
//        HashMap<String, UserData> expectedUser = new HashMap<String, UserData>();

//        UserDAO expectedUserDao = new MemoryUserDAO();
//        AuthDAO expectedAuthDAO = new MemoryAuthDao();
//        GameDAO expectedGameDAO = new MemoryGameDAO();

        //when
        try {
            userService.clear(clearRequest);
        } catch (DataAccessException e) {}

        Collection<GameData> games = gameDAO.listGames();
        AuthData authData = authDAO.findAuth(authToken);

        //then
        assert games.isEmpty();
        assertNull(authData);
    }
}