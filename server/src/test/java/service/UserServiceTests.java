package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;

import java.util.Objects;

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
            answer = new UserService(new MemoryUserDAO(), new MemoryAuthDao(), new MemoryGameDAO()).register(request);

        //then
            assert Objects.equals(expected.username(), answer.username());
            assert answer.authToken() != null;
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
            assert Objects.equals(e.getMessage(), expected.getMessage());
        }

    }
}