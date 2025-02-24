package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData) throws DataAccessException; // username already in use;
    AuthData findAuth(String username);
//    void updateAuth(AuthData authData) throws DataAccessException; // user does not exist;
    void deleteAuth(String username);
}
