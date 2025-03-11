package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createDatabaseStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256) NOT NULL,
              `blackUsername` varchar(256) NOT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` TEXT NOT NULL,
              PRIMARY KEY (`gameID`)
            )
            """
            // used to be `game` TEXT DEFAULT NULL,
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createDatabaseStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to configure database: " + e.getMessage());
        }
    }

    private void executeStatement(String statement, String errorMessageIntro, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                for (var i = 1; i <= params.length; i++) {
                    var param = params[i-1];
                    if (param instanceof String p) preparedStatement.setString(i, p);
                    else if (param instanceof Integer p) preparedStatement.setInt(i, p);
                    else if (param instanceof ChessGame p) preparedStatement.setString(i, new Gson().toJson(p)); //serialization needed
                    else if (param == null) preparedStatement.setNull(i, NULL);
                }
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(errorMessageIntro + e.getMessage());
        }
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) " +
                "VALUES (?, ?, ?, ?, ?)";
        executeStatement(statement, "Unable to create game: ",
                gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                gameData.gameName(), gameData.game());
    }

    @Override
    public GameData findGame(Integer gameID) throws DataAccessException {
        var statement = "SELECT * FROM game WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    System.out.println(resultSet.getString("game"));
                    GameData gameData = new GameData(
                            resultSet.getInt("gameID"),
                            resultSet.getString("whiteUsername"),
                            resultSet.getString("blackUsername"),
                            resultSet.getString("gameName"),
                            new Gson().fromJson(resultSet.getString("game"), ChessGame.class) //serializing chessGame
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
        var statement = "UPDATE game SET whiteUsername=? blackUsername=? game=? WHERE gameID=?";
        executeStatement(statement, "Unable to update game: ",
                gameData.whiteUsername(), gameData.blackUsername(),
                gameData.game(), gameData.gameID());
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> gamesList = new ArrayList<>();
        var statement = "SELECT * FROM game";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("game"));
                    GameData gameData = new GameData(
                            resultSet.getInt("gameID"),
                            resultSet.getString("whiteUsername"),
                            resultSet.getString("blackUsername"),
                            resultSet.getString("gameName"),
                            new Gson().fromJson(resultSet.getString("game"), ChessGame.class) //serializing chessGame
                    );
                    gamesList.add(gameData);
//                    resultSet.next();
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
