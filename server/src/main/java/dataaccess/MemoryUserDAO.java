package dataaccess;

//import java.util.Map;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    // username: [password, email]
    private HashMap<String, String> userData = new HashMap<String, String>();

    @Override
    void public create(HashMap<String, String> userData) {

    }
}
