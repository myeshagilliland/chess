package dataaccess;

import model.GameData;

public interface GameDAO {
    void create(GameData gameData);
    GameData read(Integer gameID);
    void update(GameData gameData);
    void delete(Integer gameID);
}
