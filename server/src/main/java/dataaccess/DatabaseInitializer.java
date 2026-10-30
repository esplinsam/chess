package dataaccess;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.*;

public class DatabaseInitializer {

    private static final String[] CreateStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS authentication (
                auth_token VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (auth_token)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
                game_id INT NOT NULL,
                white_username VARCHAR(255) NOT NULL,
                black_username VARCHAR(255) NOT NULL,
                game_name VARCHAR(255) NOT NULL,
                game_json JSON NOT NULL,
                PRIMARY KEY (game_id)
            )
            """
    };

    public void initialize() throws DataAccessException {
        // 0.  Initialize a DatabaseManager object to help
        DatabaseManager.createDatabase();

        // 1.  Open a connection
        try (var conn = DatabaseManager.getConnection()) {
            for (String statement : CreateStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Failed to connect");
        }

        // 2.  Create tables if they do not already exist
        // 3.
    }
}