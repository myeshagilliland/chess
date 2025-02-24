package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData gameData) throws DataAccessException; // gameID already in use
    GameData findGame(Integer gameID);
    void updateGame(GameData gameData) throws DataAccessException; // gameID does not exist
    void deleteGame(Integer gameID);
    Collection<GameData> listGames();
}
