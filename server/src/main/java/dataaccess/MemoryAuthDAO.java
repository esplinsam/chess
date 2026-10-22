package dataaccess;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements dataaccess.AuthDAO {
    @Override
    public void createAuth(model.AuthData authData)
            throws DataAccessException {}

    @Override
    public model.AuthData getAuth(String authToken)
            throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken)
            throws DataAccessException {}

    @Override
    public void clear()
            throws DataAccessException {}
}