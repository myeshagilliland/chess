package dataaccess;

import model.UserData;

public interface UserDAO {
//    <username, userData>
//    void create(userData info);
//    userData read(username user);
//    void update(userData info);
//    void delete(username user);

    void create(UserData userData);

    UserData read(String username);

    void update(UserData userData);

    void delete(String username);
}
