package service;

public class ClearService {
    private final dataaccess.UserDAO userDAO;
    private final dataaccess.AuthDAO authDAO;
    private final dataaccess.GameDAO gameDAO;

    public ClearService(
            dataaccess.UserDAO userDAO,
            dataaccess.AuthDAO authDAO,
            dataaccess.GameDAO gameDAO
    ) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void clear() throws dataaccess.DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
}