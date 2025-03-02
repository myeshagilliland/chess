package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    // gameID: gameData (gameID, whiteUsername, blackUsername, gameName, game)
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
    public void deleteGame(Integer gameID) {
        gameDatabase.remove(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
//        return (gameDatabase.values().toArray(new GameData[]{}));
        return (gameDatabase.values());
    }

}
