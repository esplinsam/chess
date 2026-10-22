package dataaccess;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, model.UserData> users = new HashMap<>();

    @Override
    public model.UserData getUser(String username)
            throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void createUserData(model.UserData userData)
            throws DataAccessException {
        users.put(userData.username(), userData);
    }

    @Override
    public void clear()
            throws DataAccessException {
        users.clear();
    }
}