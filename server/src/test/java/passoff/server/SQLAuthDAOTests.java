package passoff.server;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseInitializer;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class SQLGameDAOTests {

    private SQLGameDAO gameDAO;

    @BeforeAll
    static void initializeDatabase() throws DataAccessException {
        new DatabaseInitializer().initialize();
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    private GameData createTestGame(String gameName) {
        return new GameData(
                0,
                null,
                null,
                gameName,
                new ChessGame()
        );
    }

    @Test
    void createAndGetGame() throws DataAccessException {
        GameData input = createTestGame("Test Game");

        int gameID = gameDAO.createGame(input);
        GameData actual = gameDAO.getGame(gameID);

        GameData expected = new GameData(
                gameID,
                null,
                null,
                "Test Game",
                input.game()
        );

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getMissingGameReturnsNull() throws DataAccessException {
        Assertions.assertNull(gameDAO.getGame(999999));
    }

    @Test
    void createGamesGeneratesDistinctIDs()
            throws DataAccessException {

        int firstID = gameDAO.createGame(
                createTestGame("First Game")
        );

        int secondID = gameDAO.createGame(
                createTestGame("Second Game")
        );

        Assertions.assertNotEquals(firstID, secondID);
    }

    @Test
    void listGamesReturnsEveryGame()
            throws DataAccessException {

        int firstID = gameDAO.createGame(
                createTestGame("First Game")
        );

        int secondID = gameDAO.createGame(
                createTestGame("Second Game")
        );

        Collection<GameData> games = gameDAO.listGames();

        Set<Integer> returnedIDs = games.stream()
                .map(GameData::gameID)
                .collect(Collectors.toSet());

        Assertions.assertEquals(
                Set.of(firstID, secondID),
                returnedIDs
        );
    }

    @Test
    void listGamesReturnsEmptyCollection()
            throws DataAccessException {

        Collection<GameData> games = gameDAO.listGames();

        Assertions.assertNotNull(games);
        Assertions.assertTrue(games.isEmpty());
    }

    @Test
    void updateGameChangesStoredData()
            throws DataAccessException {

        int gameID = gameDAO.createGame(
                createTestGame("Update Game")
        );

        ChessGame changedChessGame = new ChessGame();
        changedChessGame.setTeamTurn(
                ChessGame.TeamColor.BLACK
        );

        GameData updatedGame = new GameData(
                gameID,
                "whitePlayer",
                "blackPlayer",
                "Update Game",
                changedChessGame
        );

        gameDAO.updateGame(updatedGame);

        GameData result = gameDAO.getGame(gameID);

        Assertions.assertEquals(updatedGame, result);
    }

    @Test
    void updateMissingGameThrowsException() {
        GameData nonexistentGame = new GameData(
                999999,
                null,
                null,
                "Missing Game",
                new ChessGame()
        );

        Assertions.assertThrows(
                DataAccessException.class,
                () -> gameDAO.updateGame(nonexistentGame)
        );
    }

    @Test
    void clearRemovesAllGames()
            throws DataAccessException {

        int firstID = gameDAO.createGame(
                createTestGame("First Game")
        );

        int secondID = gameDAO.createGame(
                createTestGame("Second Game")
        );

        gameDAO.clear();

        Assertions.assertAll(
                () -> Assertions.assertTrue(
                        gameDAO.listGames().isEmpty()
                ),
                () -> Assertions.assertNull(
                        gameDAO.getGame(firstID)
                ),
                () -> Assertions.assertNull(
                        gameDAO.getGame(secondID)
                )
        );
    }

    @Test
    void clearLeavesTableReusable()
            throws DataAccessException {

        gameDAO.createGame(createTestGame("Old Game"));
        gameDAO.clear();

        int newID = gameDAO.createGame(
                createTestGame("New Game")
        );

        GameData result = gameDAO.getGame(newID);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                "New Game",
                result.gameName()
        );
    }
}