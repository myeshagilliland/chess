package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData userData);
    UserData findUser(String username);
    void updateUser(UserData userData);
    void deleteUser(String username);
}
