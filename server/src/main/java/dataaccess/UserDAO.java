package dataaccess;

import model.UserData;

public interface UserDAO {
//    <username, userData>
//    void create(userData info);
//    userData read(username user);
//    void update(userData info);
//    void delete(username user);

    void createUser(UserData userData) throws DataAccessException; // username already in use
    UserData findUser(String username); //return null
    void updateUser(UserData userData) throws DataAccessException; // user does not exist
    void deleteUser(String username);
}
