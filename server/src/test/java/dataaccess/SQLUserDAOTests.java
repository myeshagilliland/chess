package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTests {

    @BeforeEach
    void freshStart() {
        try {
            UserDAO userDao = new SQLUserDAO();
            AuthDAO authDao = new SQLAuthDAO();
            GameDAO gameDao = new SQLGameDAO();
            userDao.clear();
            authDao.clear();
            gameDao.clear();
        } catch (DataAccessException e) {
            System.out.println("failed to clear database");
        }
    }

    @Test
    public void testCreateUserPositive() {
        try {
            //given
            UserDAO userDao = new SQLUserDAO();
            UserData userData = new UserData("userName", "pwd", "mail");
            //expected
            UserData expected = userData;
            //when
            userDao.createUser(userData);
            //then
            UserData answer = userDao.findUser("userName");
            assertEquals(expected.email(), answer.email());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateUserNegative() {
        try {
            //given
            UserDAO userDao = new SQLUserDAO();
            UserData userData = new UserData("userName", "pwd", "mail");
            //expected
            DataAccessException expected = new DataAccessException(
                    "Unable to create user: Duplicate entry 'userName' for key 'user.PRIMARY'");
            //when
            userDao.createUser(userData);
            try {
                userDao.createUser(userData);
            } catch (DataAccessException e) {
            //then
                assertEquals(expected.getMessage(), e.getMessage());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFindUserPositive() {
        try {
            //given
            UserDAO userDao = new SQLUserDAO();
            UserData userData = new UserData("userName", "pwd", "mail");
            userDao.createUser(userData);
            //expected
            UserData expected = userData;
            //when
            UserData answer = userDao.findUser("userName");
            //then
            assertEquals(expected.username(), answer.username());
            assertEquals(expected.email(), answer.email());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFindUserNegative() {
        try {
            //given
            UserDAO userDao = new SQLUserDAO();
            //expected
            UserData expected = null;
            //when
            UserData answer = userDao.findUser("userName");
            //then
            assertEquals(expected, answer);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testClearPositive() {
        try {
            //given
            UserDAO userDao = new SQLUserDAO();
            UserData userData = new UserData("userName", "pwd", "mail");
            //expected
            DataAccessException expected = new DataAccessException(
                    "Unable to find User: username not in user database");
            //when
            userDao.createUser(userData);
            UserData user = userDao.findUser("userName");
            assertNotNull(user);
            userDao.clear();
            try {
                userDao.findUser("userName");
            } catch (DataAccessException e) {
            //then
                assertEquals(expected.getMessage(), e.getMessage());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
