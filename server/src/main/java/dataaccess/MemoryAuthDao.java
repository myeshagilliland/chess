package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDao implements AuthDAO {
    // username: userData (username, authToken)
    private HashMap<String, AuthData> authDatabase = new HashMap<String, AuthData>();

    @Override
    public void create(AuthData authData) {
        authDatabase.put(authData.username(), authData);
    }

    @Override
    public AuthData read(String username) {
        return authDatabase.get(username);
    }

    @Override
    public void update(AuthData authData) {
        authDatabase.replace(authData.username(), authData);
    }

    @Override
    public void delete(String username) {
        authDatabase.remove(username);
    }
}
