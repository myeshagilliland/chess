package dataaccess;

import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    // gameID: userData (username, whiteUsername, blackUsername, gameName, game)
    private HashMap<Integer, GameData> gameDatabase = new HashMap<Integer, GameData>();

    @Override
    public void create(GameData gameData) {
        gameDatabase.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData read(Integer gameID) {
        return gameDatabase.get(gameID);
    }

    @Override
    public void update(GameData gameData) {
        gameDatabase.replace(gameData.gameID(), gameData);
    }

    @Override
    public void delete(Integer gameID) {
        gameDatabase.remove(gameID);
    }
}
