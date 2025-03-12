package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    // authToken: authData (authToken, username)
    private HashMap<String, AuthData> authDatabase = new HashMap<String, AuthData>();

    @Override
    public void createAuth(AuthData authData) {
        authDatabase.put(authData.authToken(), authData);
    }

    @Override
    public AuthData findAuth(String authToken) {
        return authDatabase.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        authDatabase.remove(authToken);
    }

    @Override
    public void clear() {
        authDatabase = new HashMap<String, AuthData>();
    }
}
