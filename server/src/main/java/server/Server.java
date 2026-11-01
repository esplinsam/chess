package server;

import dataaccess.DataAccessException;
import dataaccess.sql.*;
import io.javalin.Javalin;
import service.ClearService;
import service.GameService;
import service.UserService;

/**
 * Configures and manages the chess application's HTTP server.
 *
 * <p>The server constructs the application's shared in-memory data-access
 * objects, injects them into the service layer, and registers each HTTP
 * endpoint with its corresponding handler.</p>
 *
 * <p>All services receive the same DAO instances so that users,
 * authentication tokens, and games are shared consistently across
 * requests.</p>
 */
public class Server {

    /**
     * The Javalin HTTP server managed by this class.
     */
    private final Javalin javalin;

    /**
     * Constructs the server and registers all required HTTP endpoints.
     *
     * <p>The server uses in-memory DAO implementations for Phase 3.
     * These implementations can later be replaced with SQL-backed
     * implementations without changing the service interfaces.</p>
     */
    public Server() {
        // Configure Javalin to serve the provided browser interface
        // from the classpath's web directory.
        javalin = Javalin.create(
                config -> config.staticFiles.add("web")
        );

        javalin.exception(DataAccessException.class, (exception, context) -> {
            context.status(500);
            context.contentType("application/json");
            context.result("""
            {"message":"Error: unable to access database"}
            """);
        });

        try {
            new DatabaseInitializer().initialize();
        } catch (DataAccessException exception) {
            throw new RuntimeException(
                    "Error initializing database",
                    exception
            );
        }

        /*
         * Create exactly one instance of each DAO.
         *
         * These objects are shared by every service. Creating separate DAO
         * instances for different services would create separate maps and
         * prevent endpoints from seeing one another's data.
         */
        var userDAO = new SQLUserDAO();
        var authDAO = new SQLAuthDAO();
        var gameDAO = new SQLGameDAO();

        // Configure the application-clearing endpoint.
        var clearService = new ClearService(
                userDAO,
                authDAO,
                gameDAO
        );

        var clearHandler = new ClearHandler(clearService);

        // Clear all users, authentication tokens, and games.
        javalin.delete(
                "/db",
                clearHandler::clear
        );

        /*
         * Configure user and session operations.
         *
         * Registration, login, and logout share the same UserService
         * because they operate on the same user and authentication stores.
         */
        var userService = new UserService(
                userDAO,
                authDAO
        );

        var registerHandler =
                new RegisterHandler(userService);

        // Register a new user and return an authentication token.
        javalin.post(
                "/user",
                registerHandler::register
        );

        var sessionHandler =
                new SessionHandler(userService);

        // Log in an existing user and return a new authentication token.
        javalin.post(
                "/session",
                sessionHandler::login
        );

        // Log out the session identified by the authorization header.
        javalin.delete(
                "/session",
                sessionHandler::logout
        );

        /*
         * Configure game operations.
         *
         * GameService needs access to authentication data to identify
         * callers and game data to create, list, and update games.
         */
        var gameService = new GameService(
                authDAO,
                gameDAO
        );

        var gameHandler =
                new GameHandler(gameService);

        // Create a new, unclaimed chess game.
        javalin.post(
                "/game",
                gameHandler::createGame
        );

        // Return all currently stored chess games.
        javalin.get(
                "/game",
                gameHandler::listGames
        );

        // Assign the authenticated user to a game as white or black.
        javalin.put(
                "/game",
                gameHandler::joinGame
        );
    }

    /**
     * Starts the HTTP server on the requested port.
     *
     * <p>Passing port {@code 0} allows Javalin to select an available
     * port automatically, which is useful for automated tests.</p>
     *
     * @param desiredPort the port on which the server should start,
     *                    or {@code 0} to select an available port
     * @return the port on which the server is actually running
     */
    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    /**
     * Stops the HTTP server and releases its network resources.
     */
    public void stop() {
        javalin.stop();
    }
}