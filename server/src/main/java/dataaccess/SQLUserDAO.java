package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.ResultSet;
import java.sql.SQLException;
import static dataaccess.DatabaseManager.configureDatabase;
import static dataaccess.DatabaseManager.executeStatement;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        configureDatabase(createDatabaseStatements);
    }

    private final String[] createDatabaseStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            )
            """
    };

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        String encryptedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        executeStatement(statement, "Unable to create user: ", userData.username(),
                encryptedPassword, userData.email());
    }

    @Override
    public UserData findUser(String username) throws DataAccessException {
        var statement = "SELECT * FROM user WHERE username = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    UserData userData = new UserData(
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("email")
                    );
                    return userData;
                } else { return null; }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to find User: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE user";
        executeStatement(statement, "Unable to clear user database: ");
    }
}