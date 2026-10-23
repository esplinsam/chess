package dataaccess;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements dataaccess.AuthDAO {
    private final Map<String, model.AuthData> authTokens = new HashMap<>();

    @Override
    public void createAuth(model.AuthData authData)
            throws DataAccessException {
        authTokens.put(authData.authToken(), authData);
    }

    @Override
    public model.AuthData getAuth(String authToken)
            throws DataAccessException {
        return authTokens.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken)
            throws DataAccessException {
        authTokens.remove(authToken);
    }

    @Override
    public void clear()
            throws DataAccessException {
        authTokens.clear();
    }
}