package dataaccess;

import model.UserData;

public interface UserDAO {
//    <username, userData>
//    void create(userData info);
//    userData read(username user);
//    void update(userData info);
//    void delete(username user);

    void createUser(UserData userData);
    UserData findUser(String username);
    void updateUser(UserData userData);
    void deleteUser(String username);
}
