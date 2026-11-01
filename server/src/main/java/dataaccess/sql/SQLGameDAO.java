package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;
import chess.ChessGame;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.ArrayList;

import com.google.gson.Gson;

public class SQLGameDAO implements GameDAO {

    private static final Gson GSON = new Gson();

    private final String INSERT_GAME =
            """
            INSERT INTO games (
                white_username, black_username, game_name, game_json
            )
            VALUES (?, ?, ?, ?);
            """;

    public final String GET_GAME =
            """
            SELECT game_id,
                   white_username,
                   black_username,
                   game_name,
                   game_json
            FROM games
            WHERE game_id = ?;
            """;

    private final String LIST_GAMES =
            """
            SELECT game_id,
                   white_username,
                   black_username,
                   game_name,
                   game_json
            FROM games
            """;

    private final String UPDATE_GAME =
            """
            UPDATE games
            SET white_username = ?,
                black_username = ?,
                game_name = ?,
                game_json = ?
            WHERE game_id = ?
            """;

    private final String DELETE =
            """
            DELETE
            FROM games
            WHERE game_id = ?;
            """;

    private final String CLEAR =
            """
            DELETE FROM games;        
            """;

    @Override
    public int createGame(GameData game) throws DataAccessException {
        String chessGameJson = serializeGame(game.game());

        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                     INSERT_GAME,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, chessGameJson);

            int rowsCreated = statement.executeUpdate();

            if (rowsCreated != 1) {
                throw new DataAccessException(
                        "Error: unable to create game"
                );
            }

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new DataAccessException(
                    "Error: database did not generate a game ID"
            );

        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Error creating game",
                    exception
            );
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(GET_GAME)) {

            statement.setInt(1, gameID);

            try (var response = statement.executeQuery()) {
                if (response.next()) {
                    // Turn the response into an actual UserData object
                    String whiteUsername = response.getString("white_username");
                    String blackUsername = response.getString("black_username");
                    String gameName = response.getString("game_name");
                    ChessGame game = deserializeGame(
                            response.getString("game_json")
                    );

                    return new GameData(
                            gameID,
                            whiteUsername,
                            blackUsername,
                            gameName,
                            game
                    );
                }
            }

            return null;

        } catch (SQLException exception) {
            throw new DataAccessException("Unable to retrieve game", exception);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {

        Collection<GameData> games = new ArrayList<>();

        try (var connection = DatabaseManager.getConnection();
             var statement =
                     connection.prepareStatement(LIST_GAMES);
             var response = statement.executeQuery()) {

            while (response.next()) {
                ChessGame chessGame = deserializeGame(
                        response.getString("game_json")
                );

                games.add(
                        new GameData(
                                response.getInt("game_id"),
                                response.getString("white_username"),
                                response.getString("black_username"),
                                response.getString("game_name"),
                                chessGame

                        )
                );
            }

            return games;

        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Unable to list games",
                    exception
            );
        }
    }

    @Override
    public void updateGame(GameData game)
            throws DataAccessException {

        String gameJson = serializeGame(game.game());

        try (var connection = DatabaseManager.getConnection();
             var statement =
                     connection.prepareStatement(UPDATE_GAME)) {

            // Parameter positions must match the order in UPDATE_GAME.
            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gameJson);
            statement.setInt(5, game.gameID());

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new DataAccessException(
                        "Error: game does not exist"
                );
            }

        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Error updating game",
                    exception
            );
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(CLEAR)) {

            statement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to clear user data", exception);
        }
    }

    private String serializeGame(ChessGame game) {
        return GSON.toJson(game);
    }

    private ChessGame deserializeGame(String json)
            throws DataAccessException {

        try {
            return GSON.fromJson(json, ChessGame.class);
        } catch (RuntimeException exception) {
            throw new DataAccessException("Unable to deserialize game");
        }
    }
}