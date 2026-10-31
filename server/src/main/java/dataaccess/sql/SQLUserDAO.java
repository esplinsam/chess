package dataaccess.sql;

import dataaccess.UserDAO;
import model.UserData;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import java.sql.*;

public class SQLUserDAO implements UserDAO {
    // User Data Access Object class
    // Implements:
    //     createUserData -> void
    //     getUser -> UserData
    //     clear -> void

    private final String INSERT_USER =
            """
            INSERT INTO users (username, password, email)
            VALUES (?, ?, ?);
            """;

    private final String GET_USER =
            """
            SELECT *
            FROM users
            WHERE username = ?;    
            """;

    private final String CLEAR =
            """
            DELETE FROM users;        
            """;

    @Override
    public void createUserData(UserData userData)
            throws DataAccessException {
        // Extract user data to enter into table
        String username = userData.username();
        String password = userData.password();
        String email    = userData.email();

        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(INSERT_USER)) {

            // Fill in blanks in statement
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, email);

            statement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new DataAccessException("Unable to retrieve user data");
        }
    }

    @Override
    public UserData getUser(String username)
            throws DataAccessException {

        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(GET_USER)) {

            // Fill in blanks in statement
            statement.setString(1, username);

            try (var response = statement.executeQuery()) {
                if (response.next()) {
                    // Turn the response into an actual UserData object
                    String password = response.getString("password");
                    String email = response.getString("email");

                    return new UserData(
                            username,
                            password,
                            email
                    );
                }
            }

        }
        catch (SQLException exception) {
            throw new DataAccessException("No user with given username");
        }
        return null;
    }

    @ Override
    public void clear()
            throws DataAccessException {

        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(CLEAR)) {

            statement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new DataAccessException("Connection failed");
        }
    }




}