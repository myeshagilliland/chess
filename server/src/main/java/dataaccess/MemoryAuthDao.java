package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDao implements AuthDAO {
    // username: userData (username, authToken)
    private HashMap<String, AuthData> authDatabase = new HashMap<String, AuthData>();

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        if (findAuth(authData.username()) != null) {
            throw new DataAccessException("Username already authenticated");
        }
        authDatabase.put(authData.username(), authData);
    }

    @Override
    public AuthData findAuth(String username) {
        return authDatabase.get(username);
    }

//    @Override
//    public void updateAuth(AuthData authData) {
//        if (findAuth(authData.username()) == null) {
//            throw new DataAccessException("Username does not exist");
//        }
//        authDatabase.replace(authData.username(), authData);
//    }

    @Override
    public void deleteAuth(String username) {
        authDatabase.remove(username);
    }
}
