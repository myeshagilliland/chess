package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDao implements AuthDAO {
    // authToken: authData (authToken, username)
    private HashMap<String, AuthData> authDatabase = new HashMap<String, AuthData>();

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        if (findAuth(authData.authToken()) != null) {
            throw new DataAccessException("Username already authenticated");
        }
        authDatabase.put(authData.authToken(), authData);
    }

    @Override
    public AuthData findAuth(String authToken) {
        return authDatabase.get(authToken);
    }

//    @Override
//    public void updateAuth(AuthData authData) {
//        if (findAuth(authData.username()) == null) {
//            throw new DataAccessException("Username does not exist");
//        }
//        authDatabase.replace(authData.username(), authData);
//    }

    @Override
    public void deleteAuth(String authToken) {
        authDatabase.remove(authToken);
    }
}
