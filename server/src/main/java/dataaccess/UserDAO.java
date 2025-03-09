package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData userData) throws DataAccessException;
    UserData findUser(String username) throws DataAccessException;
    void clear() throws DataAccessException;
}
