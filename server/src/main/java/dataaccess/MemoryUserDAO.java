package dataaccess;

//import java.util.Map;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    // username: userData (username, password, email)
    private HashMap<String, UserData> userDatabase = new HashMap<String, UserData>();

    @Override
    public void createUser(UserData userData) {
        userDatabase.put(userData.username(), userData);
    }

    @Override
    public UserData findUser(String username) {
        return userDatabase.get(username);
    }

    @Override
    public void clear() {
        userDatabase = new HashMap<String, UserData>();
    }
}
