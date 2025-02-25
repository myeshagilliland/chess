package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Test;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;

import java.util.Objects;
import java.util.UUID;

public class UserServiceTests {

    @Test
    public void testRegisterPositive() {
        //given
        RegisterRequest request = new RegisterRequest("name", "pwd", "me@mail");

        //expected
//    set.seed(0);
//    String authToken = UUID.randomUUID().toString();
        RegisterResult expected = new RegisterResult("name", "authToken");

        //when
        RegisterResult answer;
        try {
            answer = new UserService().register(request);

        //then
            assert Objects.equals(expected.username(), answer.username());
            assert answer.authToken() != null;
        } catch (DataAccessException e) {
//            answer = new RegisterResult(null, null);
        }

    }

    @Test
    public void testRegisterNegative() {
        //given
        RegisterRequest req = new RegisterRequest("name", "pwd", "me@mail");

        //expected
        UserService userService = new UserService();
        try {
            userService.register(req);
        } catch (DataAccessException e) {
            System.out.println("Failed to register first request");
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