package dataaccess;

import model.GameData;

public interface GameDAO {
    void createGame(GameData gameData) throws DataAccessException; // gameID already in use
    GameData findGame(Integer gameID);
    void updateGame(GameData gameData) throws DataAccessException; // gameID does not exist
    void deleteGame(Integer gameID);
}
