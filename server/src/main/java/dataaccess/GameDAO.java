package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData gameData) throws DataAccessException;
    GameData findGame(Integer gameID) throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void clear() throws DataAccessException;
}
