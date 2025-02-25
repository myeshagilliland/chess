package dataaccess;

//import java.util.Map;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    // username: userData (username, password, email)
    private HashMap<String, UserData> userDatabase = new HashMap<String, UserData>();

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if (findUser(userData.username()) != null) {
            throw new DataAccessException("Error: already taken");
        }
        userDatabase.put(userData.username(), userData);
    }

    @Override
    public UserData findUser(String username) {
        return userDatabase.get(username);
    }

    @Override
    public void updateUser(UserData userData) throws DataAccessException {
        if (findUser(userData.username()) == null) {
            throw new DataAccessException("User does not exist");
        }
        userDatabase.replace(userData.username(), userData);
    }

    @Override
    public void deleteUser(String username) {
        userDatabase.remove(username);
    }
}
