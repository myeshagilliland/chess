package dataaccess;

public interface UserDAO<username, userData> {
    void create(userData info);
    userData read(username user);
    void update(userData info);
    void delete(username user);
}
