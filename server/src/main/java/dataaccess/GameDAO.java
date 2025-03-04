package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData gameData);
    GameData findGame(Integer gameID);
    void updateGame(GameData gameData);
    Collection<GameData> listGames();
    void clear();
}
