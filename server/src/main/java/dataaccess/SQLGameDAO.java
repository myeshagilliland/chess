package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import static dataaccess.DatabaseManager.configureDatabase;
import static dataaccess.DatabaseManager.executeStatement;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        configureDatabase(createDatabaseStatements);
    }

    private final String[] createDatabaseStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256) NULL,
              `blackUsername` varchar(256) NULL,
              `gameName` varchar(256) NOT NULL,
              `chessGame` TEXT NOT NULL,
              PRIMARY KEY (`gameID`)
            )
            """
    };

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, chessGame) " +
                "VALUES (?, ?, ?, ?, ?)";
        executeStatement(statement, "Unable to create game: ",
                gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                gameData.gameName(), gameData.chessGame());
    }

    @Override
    public GameData findGame(Integer gameID) throws DataAccessException {
        var statement = "SELECT * FROM game WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    GameData gameData = new GameData(
                            resultSet.getInt("gameID"),
                            resultSet.getString("whiteUsername"),
                            resultSet.getString("blackUsername"),
                            resultSet.getString("gameName"),
                            new Gson().fromJson(resultSet.getString("chessGame"), ChessGame.class)
                    );
                    return gameData;
                } else { return null; }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to find game" + e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        var statement = "UPDATE game SET whiteUsername=?, blackUsername=?, chessGame=? WHERE gameID=?";
        executeStatement(statement, "Unable to update game: ",
                gameData.whiteUsername(), gameData.blackUsername(),
                gameData.chessGame(), gameData.gameID());
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> gamesList = new ArrayList<>();
        var statement = "SELECT * FROM game";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    GameData gameData = new GameData(
                            resultSet.getInt("gameID"),
                            resultSet.getString("whiteUsername"),
                            resultSet.getString("blackUsername"),
                            resultSet.getString("gameName"),
                            new Gson().fromJson(resultSet.getString("chessGame"), ChessGame.class)
                    );
                    gamesList.add(gameData);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to find game" + e.getMessage());
        }
        return gamesList;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeStatement(statement, "Unable to clear game database: ");
    }
}
