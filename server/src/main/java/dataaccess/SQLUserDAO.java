package dataaccess;

import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    private void executeStatement(String statement, String errorMessageIntro) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(errorMessageIntro + e.getMessage());
        }
    }






    @Override
    public void createUser(UserData userData) throws DataAccessException {
//        var statement = "INSERT INTO pet (name, type, json) VALUES (?, ?, ?)";
//        var json = new Gson().toJson(pet);
//        var id = executeUpdate(statement, pet.name(), pet.type(), json);
//        return new Pet(id, pet.name(), pet.type());
        var statement = STR."INSERT INTO user (username, password, email) VALUES (\{
                userData.username()}, \{
                userData.password()}, \{
                userData.email()})";
        executeStatement(statement, "Unable to create user: ");
//        executeUpdate(statement, userData.username(), userData.password(), userData.email()); START HERE
//        statement.executeUpdate();

//        try (var conn = DatabaseManager.getConnection()) {
//            try (var preparedStatement = conn.prepareStatement(statement)) {
//                preparedStatement.executeUpdate();
//            }
//        } catch (SQLException e) {
//            throw new DataAccessException("Unable to create user: " + e.getMessage());
//        }
    }

    @Override
    public UserData findUser(String username) throws DataAccessException {
        var statement = STR."SELECT * FROM user WHERE username=\{username}";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
//                if (resultSet.next()) {
                UserData userData = new UserData(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email")
                );
                return userData;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to find User" + e.getMessage());
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
