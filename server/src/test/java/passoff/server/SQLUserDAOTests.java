package passoff.server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseInitializer;
import model.UserData;
import org.junit.jupiter.api.*;
import dataaccess.sql.SQLUserDAO;

public class SQLUserDAOTests {

    private SQLUserDAO userDAO;

    @BeforeAll
    static void initializeDatabase() throws DataAccessException {
        new DatabaseInitializer().initialize();
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    private UserData createTestUser() {
        return new UserData(
                "testUser",
                "$2a$10$exampleHashedPassword",
                "test@example.com"
        );
    }

    @Test
    void createAndGetUser() throws DataAccessException {
        UserData expected = createTestUser();

        userDAO.createUserData(expected);
        UserData actual = userDAO.getUser(expected.username());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getMissingUserReturnsNull() throws DataAccessException {
        UserData result = userDAO.getUser("missingUser");

        Assertions.assertNull(result);
    }

    @Test
    void duplicateUsernameThrowsException() throws DataAccessException {
        UserData firstUser = createTestUser();

        UserData duplicateUser = new UserData(
                firstUser.username(),
                "differentPassword",
                "different@example.com"
        );

        userDAO.createUserData(firstUser);

        Assertions.assertThrows(
                DataAccessException.class,
                () -> userDAO.createUserData(duplicateUser)
        );
    }

    @Test
    void clearRemovesUser() throws DataAccessException {
        UserData user = createTestUser();
        userDAO.createUserData(user);

        userDAO.clear();

        Assertions.assertNull(userDAO.getUser(user.username()));
    }

    @Test
    void clearLeavesTableReusable() throws DataAccessException {
        UserData user = createTestUser();

        userDAO.createUserData(user);
        userDAO.clear();
        userDAO.createUserData(user);

        Assertions.assertEquals(
                user,
                userDAO.getUser(user.username())
        );
    }

    @Test
    void preparedStatementHandlesSpecialCharacters()
            throws DataAccessException {

        UserData user = new UserData(
                "o'connor",
                "$2a$10$hash'with?characters",
                "o'connor@example.com"
        );

        userDAO.createUserData(user);

        Assertions.assertEquals(
                user,
                userDAO.getUser(user.username())
        );
    }

    @Test
    void nullRequiredFieldThrowsException() {
        UserData invalidUser = new UserData(
                "invalidUser",
                "password",
                null
        );

        Assertions.assertThrows(
                DataAccessException.class,
                () -> userDAO.createUserData(invalidUser)
        );
    }
}