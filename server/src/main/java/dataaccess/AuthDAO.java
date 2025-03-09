package dataaccess;

import model.AuthData;

import java.util.UUID;

public interface AuthDAO {
    void createAuth(AuthData authData) throws DataAccessException;
    AuthData findAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
}
