package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
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


//    public String encryptPassword (String password) {
//        return BCrypt.hashpw(password, BCrypt.gensalt());
//    }

//BCrypt.checkpw(providedClearTextPassword, hashedPassword);




    @Override
    public void createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        String encryptedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        executeStatement(statement, "Unable to create user: ", userData.username(), encryptedPassword, userData.email());
    }

    @Override
    public UserData findUser(String username) throws DataAccessException {
//        var statement = STR."SELECT * FROM user WHERE username=\{username}";
        var statement = "SELECT * FROM user WHERE username = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
//                resultSet.next();
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
//        ResultSet resultSet = executeStatement(statement, "Unable to find user: ");
//        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE user";
        executeStatement(statement, "Unable to clear user database: ");
    }
}