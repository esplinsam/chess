package server;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import service.ClearService;
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
