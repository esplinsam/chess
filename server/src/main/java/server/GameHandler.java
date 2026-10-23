package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.GameService;
import service.UnauthorizedException;
import service.request.CreateGameRequest;
import service.request.JoinGameRequest;
import service.result.CreateGameResult;
import service.result.ListGamesResult;


public class GameHandler {
    private final GameService gameService;
    private final Gson gson;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
        this.gson = new Gson();
    }


    public void createGame(Context context) {
        try {
            String authToken =
                    context.header("authorization");

            CreateGameRequest request = gson.fromJson(
                    context.body(),
                    CreateGameRequest.class
            );

            CreateGameResult result =
                    gameService.createGame(
                            request,
                            authToken
                    );

            sendSuccess(context, result);

        } catch (JsonSyntaxException
                 | BadRequestException exception) {

            sendError(context, 400, "bad request");

        } catch (UnauthorizedException exception) {

            sendError(context, 401, "unauthorized");

        } catch (DataAccessException exception) {

            sendError(
                    context,
                    500,
                    exception.getMessage()
            );
        }
    }

    public void listGames(Context context) {
        try {
            String authToken =
                    context.header("authorization");

            ListGamesResult result =
                    gameService.listGames(authToken);

            sendSuccess(context, result);

        } catch (UnauthorizedException exception) {

            sendError(context, 401, "unauthorized");

        } catch (DataAccessException exception) {

            sendError(
                    context,
                    500,
                    exception.getMessage()
            );
        }
    }

    public void joinGame(Context context) {
        try {
            String authToken =
                    context.header("authorization");

            JoinGameRequest request = gson.fromJson(
                    context.body(),
                    JoinGameRequest.class
            );

            gameService.joinGame(
                    request,
                    authToken
            );

            context.status(200);
            context.contentType("application/json");
            context.result("{}");

        } catch (JsonSyntaxException
                 | BadRequestException exception) {

            sendError(context, 400, "bad request");

        } catch (UnauthorizedException exception) {

            sendError(context, 401, "unauthorized");

        } catch (AlreadyTakenException exception) {

            sendError(context, 403, "already taken");

        } catch (DataAccessException exception) {

            sendError(
                    context,
                    500,
                    exception.getMessage()
            );
        }
    }

    private void sendSuccess(
            Context context,
            Object result
    ) {
        context.status(200);
        context.contentType("application/json");
        context.result(gson.toJson(result));
    }

    private void sendError(
            Context context,
            int statusCode,
            String description
    ) {
        ErrorResponse errorResponse =
                new ErrorResponse(
                        "Error: " + description
                );

        context.status(statusCode);
        context.contentType("application/json");
        context.result(gson.toJson(errorResponse));
    }
}