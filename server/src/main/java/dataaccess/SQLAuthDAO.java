package dataaccess;

import model.AuthData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createDatabaseStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            )
            """
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
//                    else if (param instanceof PetType p) preparedStatement.setString(i, p.toString());
                    else if (param == null) preparedStatement.setNull(i, NULL);
                }
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(errorMessageIntro + e.getMessage());
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeStatement(statement, "Unable to create auth: ", authData.authToken(), authData.username());
    }

    @Override
    public AuthData findAuth(String authToken) throws DataAccessException {
        var statement = "SELECT * FROM auth WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    AuthData authData = new AuthData(
                            resultSet.getString("authToken"),
                            resultSet.getString("username")
                    );
                    return authData;
                } else { return null; }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to find auth" + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken = ?";
        executeStatement(statement, "Unable to delete auth: ", authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeStatement(statement, "Unable to clear auth database: ");
    }
}
