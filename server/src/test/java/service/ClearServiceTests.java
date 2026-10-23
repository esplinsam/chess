package service;

import org.junit.jupiter.api.*;
import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;

public class ClearServiceTests {

    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private ClearService service;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        service = new ClearService(
                userDAO,
                authDAO,
                gameDAO
        );
    }

    @Test
    public void clearsCorrectly() throws DataAccessException {

        UserData newUser = new UserData(
                "username",
                "password",
                "email"
        );

        AuthData auth = new AuthData(
                "test-token",
                "userame"
        );

        GameData game = new GameData(
                0,
                null,
                null,
                "Test Game",
                new ChessGame()
        );

        userDAO.createUserData(newUser);
        authDAO.createAuth(auth);
        int gameID = gameDAO.createGame(game);

        Assertions.assertNotNull(
                userDAO.getUser("username")
        );

        Assertions.assertNotNull(
                gameDAO.getGame(gameID)
        );

        Assertions.assertNotNull(
                authDAO.getAuth("test-token")
        );

        Assertions.assertNotNull(
                gameDAO.getGame(gameID)
        );

        service.clear();

        Assertions.assertNull(
                userDAO.getUser("username")
        );

        Assertions.assertNull(
                authDAO.getAuth("test-token")
        );

        Assertions.assertTrue(
                gameDAO.listGames().isEmpty()
        );
    }
}