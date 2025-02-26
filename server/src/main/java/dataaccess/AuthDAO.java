package dataaccess;

import model.AuthData;

import java.util.UUID;

public interface AuthDAO {
    void createAuth(AuthData authData);
    AuthData findAuth(String authToken);
//    void updateAuth(AuthData authData);
    void deleteAuth(String authToken);
}
