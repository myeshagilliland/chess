package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void create(AuthData authData);
    AuthData read(String username);
    void update(AuthData authData);
    void delete(String username);
}
