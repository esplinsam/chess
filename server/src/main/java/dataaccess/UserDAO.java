package dataaccess;

public interface UserDAO {
    void createUserData(model.UserData userData) throws DataAccessException;

    model.UserData getUser(String username) throws DataAccessException;

    void clear() throws DataAccessException;
}