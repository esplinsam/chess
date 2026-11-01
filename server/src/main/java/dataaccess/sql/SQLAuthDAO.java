package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    private final String INSERT_AUTH =
            """
            INSERT INTO authentication (auth_token, username)
            VALUES (?, ?);
            """;

    public final String GET_AUTH =
            """
            SELECT *
            FROM authentication
            WHERE auth_token = ?;
            """;

    private final String DELETE =
            """
            DELETE
            FROM authentication
            WHERE auth_token = ?;
            """;

    private final String CLEAR =
            """
            DELETE FROM authentication;        
            """;

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

        // Extract auth data to enter into table
        String username = auth.username();
        String authToken = auth.authToken();

        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(INSERT_AUTH)) {

            // Fill in blanks in statement
            statement.setString(1, authToken);
            statement.setString(2, username);

            statement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new DataAccessException("Unable to create authentication data");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {

        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(GET_AUTH)) {

            statement.setString(1, authToken);

            try (var response = statement.executeQuery()) {
                if (response.next()) {
                    // Turn the response into an actual UserData object
                    String username = response.getString("username");

                    return new AuthData(
                            authToken,
                            username
                    );
                }
            }

            return null;

        } catch (SQLException exception) {
            throw new DataAccessException("Authentication failed");
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(DELETE)) {

            statement.setString(1, authToken);

            statement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to delete item");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(CLEAR)) {

            statement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to clear authentication data");
        }
    }
}