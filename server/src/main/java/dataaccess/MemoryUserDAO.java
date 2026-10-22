package dataaccess;

public class MemoryUserDAO implements UserDAO {
    @Override
    public model.UserData getUser(String username)
            throws DataAccessException {
        return null;
    }

    @Override
    public void createUserData(model.UserData userData)
            throws DataAccessException {}

    @Override
    public void clear()
            throws DataAccessException {}
}