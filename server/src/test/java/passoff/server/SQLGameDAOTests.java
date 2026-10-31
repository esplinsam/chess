package passoff.server;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseInitializer;
import model.GameData;
import org.junit.jupiter.api.*;
import dataaccess.sql.SQLGameDAO;

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

    private GameData createTestGame(String name) {
        return new GameData(
                0,
                null,
                null,
                name,
                new ChessGame()
        );
    }

    @Test
    void createAndGetGame() throws DataAccessException {
        GameData input = createTestGame("First Game");

        int gameID = gameDAO.createGame(input);
        GameData result = gameDAO.getGame(gameID);

        Assertions.assertNotNull(result);

        Assertions.assertAll(
                () -> Assertions.assertEquals(gameID, result.gameID()),
                () -> Assertions.assertNull(result.whiteUsername()),
                () -> Assertions.assertNull(result.blackUsername()),
                () -> Assertions.assertEquals("First Game", result.gameName()),
                () -> Assertions.assertEquals(input.game(), result.game())
        );
    }

    @Test
    void getMissingGameReturnsNull() throws DataAccessException {
        GameData result = gameDAO.getGame(999999);

        Assertions.assertNull(result);
    }

    @Test
    void createGamesGeneratesDistinctIDs() throws DataAccessException {
        int firstID = gameDAO.createGame(createTestGame("Game One"));
        int secondID = gameDAO.createGame(createTestGame("Game Two"));

        Assertions.assertNotEquals(firstID, secondID);
    }

    @Test
    void listGamesReturnsAllGames() throws DataAccessException {
        int firstID = gameDAO.createGame(createTestGame("Game One"));
        int secondID = gameDAO.createGame(createTestGame("Game Two"));

        Collection<GameData> games = gameDAO.listGames();

        Set<Integer> returnedIDs = games.stream()
                .map(GameData::gameID)
                .collect(Collectors.toSet());

        Assertions.assertEquals(Set.of(firstID, secondID), returnedIDs);
    }

    @Test
    void updateGameChangesStoredGame() throws DataAccessException {
        int gameID = gameDAO.createGame(createTestGame("Update Game"));

        ChessGame changedChessGame = new ChessGame();
        changedChessGame.setTeamTurn(ChessGame.TeamColor.BLACK);

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
        GameData missingGame = new GameData(
                999999,
                null,
                null,
                "Missing Game",
                new ChessGame()
        );

        Assertions.assertThrows(
                DataAccessException.class,
                () -> gameDAO.updateGame(missingGame)
        );
    }

    @Test
    void clearRemovesAllGames() throws DataAccessException {
        int firstID = gameDAO.createGame(createTestGame("Game One"));
        int secondID = gameDAO.createGame(createTestGame("Game Two"));

        gameDAO.clear();

        Assertions.assertAll(
                () -> Assertions.assertTrue(gameDAO.listGames().isEmpty()),
                () -> Assertions.assertNull(gameDAO.getGame(firstID)),
                () -> Assertions.assertNull(gameDAO.getGame(secondID))
        );
    }
}