package passoff.server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseInitializer;
import model.AuthData;
import org.junit.jupiter.api.*;
import dataaccess.sql.SQLAuthDAO;

public class SQLAuthDAOTests {

    private SQLAuthDAO authDAO;

    @BeforeAll
    static void initializeDatabase() throws DataAccessException {
        new DatabaseInitializer().initialize();
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        authDAO.clear();
    }

    private AuthData createTestAuth() {
        return new AuthData(
                "test-auth-token",
                "testUser"
        );
    }

    @Test
    void createAndGetAuth() throws DataAccessException {
        AuthData expected = createTestAuth();

        authDAO.createAuth(expected);
        AuthData actual = authDAO.getAuth(
                expected.authToken()
        );

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getMissingAuthReturnsNull()
            throws DataAccessException {

        AuthData result = authDAO.getAuth(
                "missing-token"
        );

        Assertions.assertNull(result);
    }

    @Test
    void duplicateTokenThrowsException()
            throws DataAccessException {

        AuthData firstAuth = createTestAuth();

        AuthData duplicateAuth = new AuthData(
                firstAuth.authToken(),
                "differentUser"
        );

        authDAO.createAuth(firstAuth);

        Assertions.assertThrows(
                DataAccessException.class,
                () -> authDAO.createAuth(duplicateAuth)
        );
    }

    @Test
    void deleteAuthRemovesToken()
            throws DataAccessException {

        AuthData authData = createTestAuth();
        authDAO.createAuth(authData);

        authDAO.deleteAuth(authData.authToken());

        Assertions.assertNull(
                authDAO.getAuth(authData.authToken())
        );
    }

    @Test
    void deleteAuthOnlyRemovesRequestedToken()
            throws DataAccessException {

        AuthData firstAuth = new AuthData(
                "first-token",
                "firstUser"
        );

        AuthData secondAuth = new AuthData(
                "second-token",
                "secondUser"
        );

        authDAO.createAuth(firstAuth);
        authDAO.createAuth(secondAuth);

        authDAO.deleteAuth(firstAuth.authToken());

        Assertions.assertAll(
                () -> Assertions.assertNull(
                        authDAO.getAuth(firstAuth.authToken())
                ),
                () -> Assertions.assertEquals(
                        secondAuth,
                        authDAO.getAuth(secondAuth.authToken())
                )
        );
    }

    @Test
    void deleteMissingAuthDoesNotThrow() {
        Assertions.assertDoesNotThrow(
                () -> authDAO.deleteAuth("missing-token")
        );
    }

    @Test
    void clearRemovesAllAuthRecords()
            throws DataAccessException {

        AuthData firstAuth = new AuthData(
                "first-token",
                "firstUser"
        );

        AuthData secondAuth = new AuthData(
                "second-token",
                "secondUser"
        );

        authDAO.createAuth(firstAuth);
        authDAO.createAuth(secondAuth);

        authDAO.clear();

        Assertions.assertAll(
                () -> Assertions.assertNull(
                        authDAO.getAuth(firstAuth.authToken())
                ),
                () -> Assertions.assertNull(
                        authDAO.getAuth(secondAuth.authToken())
                )
        );
    }

    @Test
    void clearLeavesTableReusable()
            throws DataAccessException {

        AuthData authData = createTestAuth();

        authDAO.createAuth(authData);
        authDAO.clear();
        authDAO.createAuth(authData);

        Assertions.assertEquals(
                authData,
                authDAO.getAuth(authData.authToken())
        );
    }

    @Test
    void handlesSpecialCharacters()
            throws DataAccessException {

        AuthData authData = new AuthData(
                "token-'quoted'-?",
                "user'o"
        );

        authDAO.createAuth(authData);

        Assertions.assertEquals(
                authData,
                authDAO.getAuth(authData.authToken())
        );
    }
}