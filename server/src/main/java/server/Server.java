package server;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import service.ClearService;
import service.GameService;
import service.UserService;
import io.javalin.*;


public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        var userDAO = new MemoryUserDAO();
        var gameDAO = new MemoryGameDAO();
        var authDAO = new MemoryAuthDAO();

        var clearService = new ClearService(
                userDAO,
                authDAO,
                gameDAO
        );

        var clearHandler = new ClearHandler(clearService);

        javalin.delete("/db", clearHandler::clear);

        var userService = new UserService(
                userDAO,
                authDAO
        );

        var registerHandler =
                new RegisterHandler(userService);

        javalin.post(
                "/user",
                registerHandler::register
        );

        var sessionHandler =
                new SessionHandler(userService);

        javalin.post(
                "/session",
                sessionHandler::login
        );

        javalin.delete(
                "/session",
                sessionHandler::logout
        );

        var gameService = new GameService(authDAO, gameDAO);
        var gameHandler = new GameHandler(gameService);

        javalin.post(
                "/game",
                gameHandler::createGame
        );

        javalin.get(
                "/game",
                gameHandler::listGames
        );

        javalin.put(
                "/game",
                gameHandler::joinGame
        );

        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
