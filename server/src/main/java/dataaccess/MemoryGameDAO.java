package dataaccess;

import model.GameData;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private HashMap<Integer, GameData> gameDatabase = new HashMap<Integer, GameData>();

    @Override
    public void createGame(GameData gameData) {
        gameDatabase.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData findGame(Integer gameID) {
        return gameDatabase.get(gameID);
    }

    @Override
    public void updateGame(GameData gameData) {
        gameDatabase.replace(gameData.gameID(), gameData);
    }

    @Override
    public Collection<GameData> listGames() {
        return (gameDatabase.values());
    }

    @Override
    public void clear() {
        gameDatabase = new HashMap<Integer, GameData>();
    }

}
