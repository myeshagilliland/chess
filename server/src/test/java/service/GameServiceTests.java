package service;

import dataaccess.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GameServiceTests {

    @Test
    public void testCreateGamePositive() {
        //given
        CreateGameRequest request = new CreateGameRequest("authToken", "gameName");

        //expected
        CreateGameResult expected = new CreateGameResult(1234);

        //when
        CreateGameResult answer;
        try {
            GameDAO gameDAO = new MemoryGameDAO();
            GameService gameService = new GameService(new MemoryUserDAO(), new MemoryAuthDao(), gameDAO);
            answer = gameService.createGame(request);

        //then
            assertNotNull(answer.gameID());

//            assert(gameDAO.findGame("authToken") == null);
        } catch (DataAccessException e) {}

    }

    @Test
    public void testCreateGameNegative() {
        //given
        CreateGameRequest request = new CreateGameRequest("authToken", "gameName");

        //expected
        DataAccessException expected = new DataAccessException("Error: unauthorized");

        //when
        try {
            GameDAO gameDAO = new MemoryGameDAO();
            GameService gameService = new GameService(new MemoryUserDAO(), new MemoryAuthDao(), gameDAO);
            gameService.createGame(request);

        } catch (DataAccessException e) {

        //then
            assertEquals(e.getMessage(), expected.getMessage());
        }

    }
}