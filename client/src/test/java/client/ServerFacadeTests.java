package client;

import chess.ChessGame;
import client.dto.AuthResponse;
import client.dto.GameSummary;
import client.dto.ListGamesResponse;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static final String USERNAME = "testUsername";
    private static final String PASSWORD = "testPassword";
    private static final String EMAIL = "test@email.com";
    private static Server server;
    private static String serverUrl;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    /**
     * Clears all database data before each test so tests remain independent.
     */
    @BeforeEach
    public void clearDatabase() throws IOException {
        HttpURLConnection connection = null;

        try {
            var endpoint = URI.create(serverUrl + "/db").toURL();
            connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestMethod("DELETE");

            int statusCode = connection.getResponseCode();

            assertEquals(
                    200,
                    statusCode,
                    "The database could not be cleared before the test"
            );

            // Consume and close the response body.
            try (var response = connection.getInputStream()) {
                response.readAllBytes();
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Stops the server after every test has finished.
     */
    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    /*
     * Registration tests
     */

    @Test
    @DisplayName("Register successfully")
    public void registerPositive() throws ResponseException {
        AuthResponse response = facade.register(
                USERNAME,
                PASSWORD,
                EMAIL
        );

        assertNotNull(response);
        assertEquals(USERNAME, response.username());
        assertNotNull(response.authToken());
        assertFalse(response.authToken().isBlank());
    }

    @Test
    @DisplayName("Register duplicate username")
    public void registerNegativeDuplicateUsername()
            throws ResponseException {

        facade.register(USERNAME, PASSWORD, EMAIL);

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> facade.register(
                        USERNAME,
                        "differentPassword",
                        "different@example.com"
                )
        );

        assertEquals(403, exception.getStatusCode());
        assertNotNull(exception.getMessage());
        assertFalse(exception.getMessage().isBlank());
    }

    /*
     * Login tests
     */

    @Test
    @DisplayName("Login successfully")
    public void loginPositive() throws ResponseException {
        facade.register(USERNAME, PASSWORD, EMAIL);

        AuthResponse response = facade.login(
                USERNAME,
                PASSWORD
        );

        assertNotNull(response);
        assertEquals(USERNAME, response.username());
        assertNotNull(response.authToken());
        assertFalse(response.authToken().isBlank());
    }

    @Test
    @DisplayName("Login with incorrect password")
    public void loginNegativeWrongPassword()
            throws ResponseException {

        facade.register(USERNAME, PASSWORD, EMAIL);

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> facade.login(
                        USERNAME,
                        "incorrectPassword"
                )
        );

        assertEquals(401, exception.getStatusCode());
        assertNotNull(exception.getMessage());
    }

    /*
     * Logout tests
     */

    @Test
    @DisplayName("Logout successfully")
    public void logoutPositive() throws ResponseException {
        AuthResponse registration = registerStandardUser();
        String authToken = registration.authToken();

        assertDoesNotThrow(() -> facade.logout(authToken));

        // Verify that the server actually invalidated the token.
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> facade.listGames(authToken)
        );

        assertEquals(401, exception.getStatusCode());
    }

    @Test
    @DisplayName("Logout with invalid authorization")
    public void logoutNegativeInvalidToken() {
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> facade.logout("invalid-auth-token")
        );

        assertEquals(401, exception.getStatusCode());
    }

    /*
     * Create-game tests
     */

    @Test
    @DisplayName("Create game successfully")
    public void createGamePositive() throws ResponseException {
        AuthResponse registration = registerStandardUser();

        var response = facade.createGame(
                registration.authToken(),
                "Test Game"
        );

        assertNotNull(response);
        assertTrue(
                response.gameID() > 0,
                "The server should return a positive game ID"
        );
    }

    @Test
    @DisplayName("Create game without authorization")
    public void createGameNegativeUnauthorized() {
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> facade.createGame(
                        "invalid-auth-token",
                        "Unauthorized Game"
                )
        );

        assertEquals(401, exception.getStatusCode());
    }

    /*
     * List-games tests
     */

    @Test
    @DisplayName("List games successfully")
    public void listGamesPositive() throws ResponseException {
        AuthResponse registration = registerStandardUser();
        String authToken = registration.authToken();

        int firstGameID = facade.createGame(
                authToken,
                "First Game"
        ).gameID();

        int secondGameID = facade.createGame(
                authToken,
                "Second Game"
        ).gameID();

        ListGamesResponse response = facade.listGames(authToken);

        assertNotNull(response);
        assertNotNull(response.games());
        assertEquals(2, response.games().size());

        assertTrue(containsGame(
                response,
                firstGameID,
                "First Game"
        ));

        assertTrue(containsGame(
                response,
                secondGameID,
                "Second Game"
        ));
    }

    @Test
    @DisplayName("List games without authorization")
    public void listGamesNegativeUnauthorized() {
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> facade.listGames("invalid-auth-token")
        );

        assertEquals(401, exception.getStatusCode());
    }

    /*
     * Join-game tests
     */

    @Test
    @DisplayName("Join game successfully")
    public void joinGamePositive() throws ResponseException {
        AuthResponse registration = registerStandardUser();
        String authToken = registration.authToken();

        int gameID = facade.createGame(
                authToken,
                "Join Test"
        ).gameID();

        assertDoesNotThrow(() -> facade.joinGame(
                gameID,
                authToken,
                        ChessGame.TeamColor.WHITE
                ));

        ListGamesResponse response = facade.listGames(authToken);

        GameSummary joinedGame = findGame(response, gameID);

        assertNotNull(joinedGame);
        assertEquals(USERNAME, joinedGame.whiteUsername());
        assertNull(joinedGame.blackUsername());
    }

    @Test
    @DisplayName("Join nonexistent game")
    public void joinGameNegativeBadGameID()
            throws ResponseException {

        AuthResponse registration = registerStandardUser();

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> facade.joinGame(
                        999999,
                        registration.authToken(),
                                        ChessGame.TeamColor.WHITE
                                )
        );

        assertEquals(400, exception.getStatusCode());
    }

    @Test
    @DisplayName("Join occupied player color")
    public void joinGameNegativeColorTaken()
            throws ResponseException {

        AuthResponse firstPlayer = facade.register(
                "firstPlayer",
                "passwordOne",
                "first@example.com"
        );

        int gameID = facade.createGame(
                firstPlayer.authToken(),
                "Occupied Game"
        ).gameID();

        facade.joinGame(
                gameID,
                firstPlayer.authToken(),
                        ChessGame.TeamColor.WHITE
                );

        AuthResponse secondPlayer = facade.register(
                "secondPlayer",
                "passwordTwo",
                "second@example.com"
        );

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> facade.joinGame(
                        gameID,
                        secondPlayer.authToken(),
                                        ChessGame.TeamColor.WHITE
                                )
        );

        assertEquals(403, exception.getStatusCode());
    }

    @Test
    @DisplayName("Join game without authorization")
    public void joinGameNegativeUnauthorized()
            throws ResponseException {

        AuthResponse registration = registerStandardUser();

        int gameID = facade.createGame(
                registration.authToken(),
                "Authorization Test"
        ).gameID();

        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> facade.joinGame(
                        gameID,
                        "invalid-auth-token",
                                        ChessGame.TeamColor.BLACK
                                )
        );

        assertEquals(401, exception.getStatusCode());
    }

    /*
     * Test helper methods
     */

    private AuthResponse registerStandardUser()
            throws ResponseException {

        return facade.register(
                USERNAME,
                PASSWORD,
                EMAIL
        );
    }

    private boolean containsGame(
            ListGamesResponse response,
            int gameID,
            String gameName
    ) {
        return response.games()
                .stream()
                .anyMatch(game ->
                        game.gameID() == gameID &&
                                gameName.equals(game.gameName())
                );
    }

    private GameSummary findGame(
            ListGamesResponse response,
            int gameID
    ) {
        return response.games()
                .stream()
                .filter(game -> game.gameID() == gameID)
                .findFirst()
                .orElse(null);
    }

}
