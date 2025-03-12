package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTests {

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
    public void testCreateAuthPositive() {
        try {
            //given
            AuthDAO authDao = new SQLAuthDAO();
            AuthData authData = new AuthData("authToken", "userName");
            //expected
            AuthData expected = authData;
            //when
            authDao.createAuth(authData);
            AuthData answer = authDao.findAuth("authToken");
            //then
            assertEquals(expected.username(), answer.username());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateAuthNegative() {
        try {
            //given
            AuthDAO authDao = new SQLAuthDAO();
            AuthData authData = new AuthData("authToken", "userName");
            //expected
            DataAccessException expected = new DataAccessException(
                    "Unable to create auth: Duplicate entry 'authToken' for key 'auth.PRIMARY'");
            //when
            authDao.createAuth(authData);
            try {
                authDao.createAuth(authData);
            } catch (DataAccessException e) {
            //then
                assertEquals(expected.getMessage(), e.getMessage());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFindAuthPositive() {
        try {
            //given
            AuthDAO authDao = new SQLAuthDAO();
            AuthData authData = new AuthData("authToken", "userName");
            authDao.createAuth(authData);
            //expected
            AuthData expected = authData;
            //when
            AuthData answer = authDao.findAuth("authToken");
            //then
            assertEquals(expected.authToken(), answer.authToken());
            assertEquals(expected.username(), answer.username());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFindAuthNegative() {
        try {
            //given
            AuthDAO authDao = new SQLAuthDAO();
            //expected
            AuthData expected = null;
            //when
            AuthData answer = authDao.findAuth("authToken");
            //then
            assertEquals(expected, answer);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDeleteAuthPositive() {
        try {
            //given
            AuthDAO authDao = new SQLAuthDAO();
            AuthData authData = new AuthData("authToken", "userName");
            authDao.createAuth(authData);
            //expected
            //when
            authDao.deleteAuth("authToken");
            //then
            AuthData answer = authDao.findAuth("authToken");
            assertNull(answer);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDeleteAuthNegative() {
        try {
            //given
            AuthDAO authDao = new SQLAuthDAO();
            //expected
            DataAccessException expected = new DataAccessException(
                    "Unable to create auth: Duplicate entry 'authToken' for key 'auth.PRIMARY'");
            //when
            try {
                authDao.deleteAuth("authToken");
            } catch (DataAccessException e) {
            //then
                assertEquals(expected.getMessage(),e.getMessage());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testClearPositive() {
        try {
            //given
            AuthDAO authDao = new SQLAuthDAO();
            AuthData authData = new AuthData("authToken", "userName");
            //expected
            AuthData expected = null;
            //when
            authDao.createAuth(authData);
            AuthData auth = authDao.findAuth("authToken");
            assertNotNull(auth);
            authDao.clear();
            AuthData answer = authDao.findAuth("authToken");
            //then
            assertEquals(expected, answer);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
