package client;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import client.dto.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class ServerFacade {
    private static final Gson GSON = new Gson();

    private final String url;

    public ServerFacade(String url) {
        this.url = url;
    }

    /**
     * Registers a new user
     */
    public AuthResponse register(String username, String password, String email)
            throws ResponseException {

        var request = new RegisterRequest(username, password, email);

        return makeRequest(
                "POST",
                "/user",
                null,
                request,
                AuthResponse.class
        );
    }

    /**
     * Logs a user in
     */
    public AuthResponse login(String username, String password)
            throws ResponseException {

        var request = new LoginRequest(username, password);

        return makeRequest(
                "POST",
                "/session",
                null,
                request,
                AuthResponse.class
        );
    }

    /**
     * Logs a user out
     */
    public void logout(String authToken)
            throws ResponseException {
        makeRequest(
                "DELETE",
                "/session",
                authToken,
                null,
                null
        );
    }

    /**
     * Creates a new game
     */
    public CreateGameResponse createGame(String authToken, String gameName)
            throws ResponseException {

        var request = new CreateGameRequest(gameName);

        return makeRequest(
                "POST",
                "/game",
                authToken,
                request,
                CreateGameResponse.class
        );
    }

    /**
     * Lists a given users games
     */
    public ListGamesResponse listGames(String authToken)
            throws ResponseException {

        return makeRequest(
                "GET",
                "/game",
                authToken,
                null,
                ListGamesResponse.class
        );
    }

    /**
     * Allows a user to join a game
     */
    public void joinGame(int gameID, String authToken, TeamColor playerColor)
            throws ResponseException {

        var request = new JoinGameRequest(playerColor, gameID);

        makeRequest(
                "PUT",
                "/game",
                authToken,
                request,
                null
        );
    }

    /**
     * Sends an HTTP request and converts its JSON response into the
     * requested response type.
     */
    private <T> T makeRequest(
            String method,
            String path,
            String authToken,
            Object requestBody,
            Class<T> responseClass
    ) throws ResponseException {

        HttpURLConnection connection = null;

        try {
            var endpoint = URI.create(url + path).toURL();
            connection = (HttpURLConnection) endpoint.openConnection();

            connection.setRequestMethod(method);
            connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
            );
            connection.setRequestProperty(
                    "Accept",
                    "application/json"
            );

            if (authToken != null && !authToken.isBlank()) {
                connection.setRequestProperty(
                        "authorization",
                        authToken
                );
            }

            if (requestBody != null) {
                connection.setDoOutput(true);

                String json = GSON.toJson(requestBody);

                try (var output = connection.getOutputStream()) {
                    output.write(json.getBytes(StandardCharsets.UTF_8));
                }
            }

            int statusCode = connection.getResponseCode();
            boolean successful =
                    statusCode >= 200 && statusCode < 300;

            String responseJson = readResponse(
                    successful
                            ? connection.getInputStream()
                            : connection.getErrorStream()
            );

            if (!successful) {
                throw createResponseException(
                        statusCode,
                        responseJson
                );
            }

            // Logout and joinGame do not require response data.
            if (responseClass == null) {
                return null;
            }

            if (responseJson.isBlank()) {
                throw new ResponseException(
                        "Error: server returned an empty response"
                );
            }

            return GSON.fromJson(responseJson, responseClass);

        } catch (ResponseException exception) {
            throw exception;
        } catch (IOException | RuntimeException exception) {
            throw new ResponseException(
                    "Error communicating with the server",
                    exception
            );
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Reads the complete HTTP response body.
     */
    private String readResponse(InputStream stream)
            throws IOException {

        if (stream == null) {
            return "";
        }

        try (stream) {
            return new String(
                    stream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }

    /**
     * Extracts the server's error message from an unsuccessful response.
     */
    private ResponseException createResponseException(
            int statusCode,
            String responseJson
    ) {

        String message = "Error: request failed";

        if (!responseJson.isBlank()) {
            try {
                ErrorResponse error =
                        GSON.fromJson(responseJson, ErrorResponse.class);

                if (error != null &&
                        error.message() != null &&
                        !error.message().isBlank()) {

                    message = error.message();
                }
            } catch (RuntimeException ignored) {
                // Use the generic message if the error JSON is malformed.
            }
        }

        return new ResponseException(message);
    }

    private record RegisterRequest(
            String username,
            String password,
            String email
    ) {}

    private record LoginRequest(
            String username,
            String password
    ) {}

    private record CreateGameRequest(
            String gameName
    ) {}

    private record JoinGameRequest(
            ChessGame.TeamColor playerColor,
            int gameID
    ) {}


}