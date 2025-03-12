package dataaccess;

import model.AuthData;
import java.sql.ResultSet;
import java.sql.SQLException;
import static dataaccess.DatabaseManager.configureDatabase;
import static dataaccess.DatabaseManager.executeStatement;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        configureDatabase(createDatabaseStatements);
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



    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeStatement(statement, "Unable to create auth: ",
                authData.authToken(), authData.username());
    }

    @Override
    public AuthData findAuth(String authToken) throws DataAccessException {
        var statement = "SELECT * FROM auth WHERE authToken=?";
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
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeStatement(statement, "Unable to delete auth: ", authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeStatement(statement, "Unable to clear auth database: ");
    }
}
