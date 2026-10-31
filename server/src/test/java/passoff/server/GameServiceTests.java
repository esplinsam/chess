package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.CreateGameRequest;
import service.request.JoinGameRequest;
import service.result.CreateGameResult;
import service.result.ListGamesResult;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private static final String USERNAME = "Samuel";
    private static final String AUTH_TOKEN = "valid-token";

    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private GameService gameService;

    @BeforeEach
    public void setup() throws DataAccessException {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(authDAO, gameDAO);

        authDAO.createAuth(new AuthData(AUTH_TOKEN, USERNAME));
    }

    @Test
    public void createGameSuccess() throws Exception {
        CreateGameResult result = gameService.createGame(
                new CreateGameRequest("Test Game"),
                AUTH_TOKEN
        );

        assertTrue(result.gameID() > 0);

        GameData storedGame = gameDAO.getGame(result.gameID());
        assertNotNull(storedGame);
        assertEquals("Test Game", storedGame.gameName());
        assertNull(storedGame.whiteUsername());
        assertNull(storedGame.blackUsername());
        assertNotNull(storedGame.game());
    }

    @Test
    public void createGamesHaveUniqueIDs() throws Exception {
        int firstID = gameService.createGame(
                new CreateGameRequest("First"),
                AUTH_TOKEN
        ).gameID();

        int secondID = gameService.createGame(
                new CreateGameRequest("Second"),
                AUTH_TOKEN
        ).gameID();

        assertNotEquals(firstID, secondID);
    }

    @Test
    public void createGameMissingNameFails() {
        assertThrows(
                BadRequestException.class,
                () -> gameService.createGame(
                        new CreateGameRequest(null),
                        AUTH_TOKEN
                )
        );
    }

    @Test
    public void createGameInvalidAuthenticationFails() {
        assertThrows(
                UnauthorizedException.class,
                () -> gameService.createGame(
                        new CreateGameRequest("Test Game"),
                        "invalid-token"
                )
        );
    }

    @Test
    public void listGamesEmptySuccess() throws Exception {
        ListGamesResult result = gameService.listGames(AUTH_TOKEN);

        assertNotNull(result.games());
        assertTrue(result.games().isEmpty());
    }

    @Test
    public void listGamesReturnsAllGames() throws Exception {
        gameService.createGame(
                new CreateGameRequest("First"),
                AUTH_TOKEN
        );
        gameService.createGame(
                new CreateGameRequest("Second"),
                AUTH_TOKEN
        );

        ListGamesResult result = gameService.listGames(AUTH_TOKEN);

        assertEquals(2, result.games().size());
        assertTrue(result.games().stream()
                .anyMatch(game -> game.gameName().equals("First")));
        assertTrue(result.games().stream()
                .anyMatch(game -> game.gameName().equals("Second")));
    }

    @Test
    public void listGamesInvalidAuthenticationFails() {
        assertThrows(
                UnauthorizedException.class,
                () -> gameService.listGames("invalid-token")
        );
    }

    @Test
    public void joinWhiteSuccess() throws Exception {
        int gameID = createStoredGame("Join White");

        gameService.joinGame(
                new JoinGameRequest(
                        ChessGame.TeamColor.WHITE,
                        gameID
                ),
                AUTH_TOKEN
        );

        GameData updatedGame = gameDAO.getGame(gameID);
        assertEquals(USERNAME, updatedGame.whiteUsername());
        assertNull(updatedGame.blackUsername());
    }

    @Test
    public void sameUserCanJoinBothColors() throws Exception {
        int gameID = createStoredGame("Both Colors");

        gameService.joinGame(
                new JoinGameRequest(
                        ChessGame.TeamColor.WHITE,
                        gameID
                ),
                AUTH_TOKEN
        );

        gameService.joinGame(
                new JoinGameRequest(
                        ChessGame.TeamColor.BLACK,
                        gameID
                ),
                AUTH_TOKEN
        );

        GameData updatedGame = gameDAO.getGame(gameID);
        assertEquals(USERNAME, updatedGame.whiteUsername());
        assertEquals(USERNAME, updatedGame.blackUsername());
    }

    @Test
    public void joiningOccupiedColorFails() throws Exception {
        int gameID = createStoredGame("Occupied Color");

        gameService.joinGame(
                new JoinGameRequest(
                        ChessGame.TeamColor.WHITE,
                        gameID
                ),
                AUTH_TOKEN
        );

        assertThrows(
                AlreadyTakenException.class,
                () -> gameService.joinGame(
                        new JoinGameRequest(
                                ChessGame.TeamColor.WHITE,
                                gameID
                        ),
                        AUTH_TOKEN
                )
        );
    }

    @Test
    public void joinMissingColorFails() throws Exception {
        int gameID = createStoredGame("Missing Color");

        assertThrows(
                BadRequestException.class,
                () -> gameService.joinGame(
                        new JoinGameRequest(null, gameID),
                        AUTH_TOKEN
                )
        );
    }

    @Test
    public void joinMissingGameIDFails() {
        assertThrows(
                BadRequestException.class,
                () -> gameService.joinGame(
                        new JoinGameRequest(
                                ChessGame.TeamColor.WHITE,
                                null
                        ),
                        AUTH_TOKEN
                )
        );
    }

    @Test
    public void joinNonexistentGameFails() {
        assertThrows(
                BadRequestException.class,
                () -> gameService.joinGame(
                        new JoinGameRequest(
                                ChessGame.TeamColor.WHITE,
                                9999
                        ),
                        AUTH_TOKEN
                )
        );
    }

    @Test
    public void joinInvalidAuthenticationFails() throws Exception {
        int gameID = createStoredGame("Bad Authentication");

        assertThrows(
                UnauthorizedException.class,
                () -> gameService.joinGame(
                        new JoinGameRequest(
                                ChessGame.TeamColor.WHITE,
                                gameID
                        ),
                        "invalid-token"
                )
        );
    }

    private int createStoredGame(String gameName)
            throws Exception {
        return gameService.createGame(
                new CreateGameRequest(gameName),
                AUTH_TOKEN
        ).gameID();
    }
}
