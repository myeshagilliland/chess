package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;
import requestresult.*;

import java.util.Collection;

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
//            UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDao(), new MemoryGameDAO());
            UserService userService = null;
            try {
                userService = new UserService(new SQLUserDAO(), new SQLAuthDAO(), new SQLGameDAO());
            } catch (DataAccessException e) {}
            assertNotNull(userService);
            answer = userService.register(request);

        //then
            assertEquals(expected.username(), answer.username());
            assertNotNull(answer.authToken());
        } catch (ServiceException e) {}

    }

    @Test
    public void testRegisterNegative() {
        //given
        RegisterRequest req = new RegisterRequest("name", "pwd", "me@mail");

        //expected
//        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDao(), new MemoryGameDAO());
        UserService userService = null;
        try {
            userService = new UserService(new SQLUserDAO(), new SQLAuthDAO(), new SQLGameDAO());
        } catch (DataAccessException e) {}
        assertNotNull(userService);
        try {
            userService.register(req);
        } catch (ServiceException e) {
            System.out.println("Failed to register first request"); //this is executing, why?
        }
        AlreadyTakenException expected = new AlreadyTakenException();

        //when
        try {
            userService.register(req);
        } catch (ServiceException e) {

        //then
            assertEquals(expected.getMessage(), e.getMessage());
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
//            UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDao(), new MemoryGameDAO());
            UserService userService = null;
            try {
                userService = new UserService(new SQLUserDAO(), new SQLAuthDAO(), new SQLGameDAO());
            } catch (DataAccessException e) {}
            assertNotNull(userService);
            answer = userService.login(request);

        //then
            assertEquals(expected.username(), answer.username());
            assertNotNull(answer.authToken());
        } catch (ServiceException e) {}

    }

    @Test
    public void testLoginNegative() {
        //given
        LoginRequest req = new LoginRequest("name", "pwd");

        //expected
        UnauthorizedException expected = new UnauthorizedException();

        //when
//        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDao(), new MemoryGameDAO());
        UserService userService = null;
        try {
            userService = new UserService(new SQLUserDAO(), new SQLAuthDAO(), new SQLGameDAO());
        } catch (DataAccessException e) {}
        assertNotNull(userService);
        try {
            userService.login(req);
        } catch (ServiceException e) {

        //then
            assertEquals(expected.getMessage(), e.getMessage());
        }

    }

    @Test
    public void testLogoutPositive() {
        //given
        LogoutRequest request = new LogoutRequest("authToken");

        //expected

        //when
        try {
//            AuthDAO authDAO = new MemoryAuthDao();
            AuthDAO authDAO = new SQLAuthDAO();
//            UserService userService = new UserService(new MemoryUserDAO(), authDAO, new MemoryGameDAO());
            UserService userService = null;
            try {
                userService = new UserService(new SQLUserDAO(), authDAO, new SQLGameDAO());
            } catch (DataAccessException e) {}
            assertNotNull(userService);
            userService.logout(request);

        //then
            assert(authDAO.findAuth("authToken") == null);
        } catch (Exception e) {}
    }

    @Test
    public void testLogoutNegative() {
        //given
        LogoutRequest request = new LogoutRequest("authToken");

        //expected
        UnauthorizedException expected = new UnauthorizedException();

        //when
        try {
//            UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDao(), new MemoryGameDAO());
            UserService userService = null;
            try {
                userService = new UserService(new SQLUserDAO(), new SQLAuthDAO(), new SQLGameDAO());
            } catch (DataAccessException e) {}
            assertNotNull(userService);
            userService.logout(request);
        } catch (ServiceException e) {

        //then
            assertEquals(expected.getMessage(), e.getMessage());
        }
    }

    @Test
    public void testClearPositive() {
        //given
//        UserDAO userDAO = new MemoryUserDAO();
//        AuthDAO authDAO = new MemoryAuthDao();
//        GameDAO gameDAO = new MemoryGameDAO();

        UserDAO userDAO = null;
        try {
            userDAO = new SQLUserDAO();
        } catch (DataAccessException e) {}
        AuthDAO authDAO = new SQLAuthDAO();
        GameDAO gameDAO = new SQLGameDAO();

//        UserService userService = new UserService(userDao, authDAO, gameDAO);
        UserService userService = null;
        try {
            userService = new UserService(new SQLUserDAO(), new SQLAuthDAO(), new SQLGameDAO());
        } catch (DataAccessException e) {}
        assertNotNull(userService);
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult userInfo = null;
        try {
            userInfo = userService.register(registerRequest);
        } catch (ServiceException e) {}

        String authToken = userInfo.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "gameName1");
        GameService gameService = new GameService(userDAO, authDAO, gameDAO);
        try {
            gameService.createGame(createGameRequest);
        } catch (ServiceException e) {}

        ClearRequest clearRequest = new ClearRequest();

        //expected

        //when
        try {
            userService.clear(clearRequest);
        } catch (ServiceException e) {}

        Collection<GameData> games = null;
        AuthData authData = null;
        try {
            games = gameDAO.listGames();
            authData = authDAO.findAuth(authToken);
        } catch (DataAccessException e) {}

        //then
        assert games.isEmpty();
        assertNull(authData);
    }
}