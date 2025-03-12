package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTests {

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
    public void testCreateGamePositive() {
        try {
            //given
            GameDAO gameDao = new SQLGameDAO();
            GameData gameData = new GameData(123, "white", "black", "name", new ChessGame());
            //expected
            GameData expected = gameData;
            //when
            gameDao.createGame(gameData);
            GameData answer = gameDao.findGame(123);
            //then
            assertEquals(expected.gameName(), answer.gameName());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGameNegative() {
        try {
            //given
            GameDAO gameDao = new SQLGameDAO();
            GameData gameData = new GameData(123, "white", "black", "name", new ChessGame());
            //expected
            DataAccessException expected = new DataAccessException(
                    "Unable to create game: Duplicate entry '123' for key 'game.PRIMARY'");
            //when
            gameDao.createGame(gameData);
            try {
                gameDao.createGame(gameData);
            } catch (DataAccessException e) {
            //then
                assertEquals(expected.getMessage(), e.getMessage());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFindGamePositive() {
        try {
            //given
            GameDAO gameDao = new SQLGameDAO();
            GameData gameData = new GameData(123, "white", "black", "name", new ChessGame());
            gameDao.createGame(gameData);
            //expected
            GameData expected = gameData;
            //when
            GameData answer = gameDao.findGame(123);
            //then
            assertEquals(expected.gameID(), answer.gameID());
            assertEquals(expected.gameName(), answer.gameName());
            assertEquals(expected.whiteUsername(), answer.whiteUsername());
            assertEquals(expected.blackUsername(), answer.blackUsername());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFindGameNegative() {
        try {
            //given
            GameDAO gameDao = new SQLGameDAO();
            //expected
            GameData expected = null;
            //when
            GameData answer = gameDao.findGame(111);
            //then
            assertEquals(expected, answer);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUpdateGamePositive() {
        try {
            //given
            GameDAO gameDao = new SQLGameDAO();
            GameData gameData = new GameData(123, null, "black", "name", new ChessGame());
            gameDao.createGame(gameData);
            //expected
            GameData expected = new GameData(123, "white", "black", "name", gameData.chessGame());
            //when
            gameDao.updateGame(expected);
            GameData answer = gameDao.findGame(123);
            //then
            assertEquals(expected.whiteUsername(), answer.whiteUsername());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUpdateGameNegative() {
        try {
            //given
            GameDAO gameDao = new SQLGameDAO();
            GameData gameData = new GameData(123, null, "black", "name", new ChessGame());
            gameDao.createGame(gameData);
            //expected
            GameData expected = new GameData(111, "white", "black", "name", gameData.chessGame());
            //when
            gameDao.updateGame(expected);
            GameData answer = gameDao.findGame(123);
            //then
            assertNotEquals(expected.whiteUsername(), answer.whiteUsername());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testListGamesPositive() {
        try {
            //given
            GameDAO gameDao = new SQLGameDAO();
            GameData gameData1 = new GameData(123, "white1", "black1", "name1", new ChessGame());
            GameData gameData2 = new GameData(456, "white2", "black2", "name2", new ChessGame());
            //expected
            Collection<GameData> expected = new ArrayList<>();
            expected.add(gameData1);
            expected.add(gameData2);
            //when
            gameDao.createGame(gameData1);
            gameDao.createGame(gameData2);
            Collection<GameData> answer = gameDao.listGames();
            //then
            assertEquals(expected.size(), answer.size());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testListGamesNegative() {
        try {
            //given
            GameDAO gameDao = new SQLGameDAO();
            GameData gameData1 = new GameData(123, "white1", "black1", "name1", new ChessGame());
            GameData gameData2 = new GameData(456, "white2", "black2", "name2", new ChessGame());
            //expected
            Collection<GameData> expected = new ArrayList<>();
            expected.add(gameData1);
            expected.add(gameData2);
            //when
            gameDao.createGame(gameData1);
//            gameDao.createGame(gameData2);
            Collection<GameData> answer = gameDao.listGames();
            //then
            assertNotEquals(expected.size(), answer.size());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testClearPositive() {
        try {
            //given
            GameDAO gameDao = new SQLGameDAO();
            GameData gameData = new GameData(123, "white", "black", "name", new ChessGame());
            //expected
            GameData expected = null;
            //when
            gameDao.createGame(gameData);
            GameData game = gameDao.findGame(123);
            assertNotNull(game);
            gameDao.clear();
            GameData answer = gameDao.findGame(123);
            //then
            assertEquals(expected, answer);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
