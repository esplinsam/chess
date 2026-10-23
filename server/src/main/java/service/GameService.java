package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import service.request.CreateGameRequest;
import service.request.JoinGameRequest;
import service.result.CreateGameResult;
import service.result.GameListEntry;
import service.result.ListGamesResult;

import java.util.ArrayList;
import java.util.Collection;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    private AuthData authenticate(String authToken)
            throws UnauthorizedException,
            DataAccessException {

        AuthData authData = authDAO.getAuth(authToken);

        if (authData == null) {
            throw new UnauthorizedException(
                    "Invalid authentication token"
            );
        }

        return authData;
    }

    public CreateGameResult createGame(
            CreateGameRequest request,
            String authToken
    ) throws UnauthorizedException,
            BadRequestException,
            DataAccessException {

        authenticate(authToken);

        if (request == null
                || request.gameName() == null) {

            throw new BadRequestException(
                    "Missing game name"
            );
        }

        GameData gameData = new GameData(
                0,
                null,
                null,
                request.gameName(),
                new ChessGame()
        );

        int gameID = gameDAO.createGame(gameData);

        return new CreateGameResult(gameID);
    }

    public ListGamesResult listGames(String authToken)
            throws UnauthorizedException,
            DataAccessException {

        authenticate(authToken);

        Collection<GameData> storedGames = gameDAO.listGames();

        Collection<GameListEntry> entries = new ArrayList<>();

        for (GameData gameData : storedGames) {
            GameListEntry entry = new GameListEntry(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName()
            );

            entries.add(entry);
        }

        return new ListGamesResult(entries);
    }

    public void joinGame(
            JoinGameRequest request,
            String authToken
    ) throws UnauthorizedException,
            BadRequestException,
            AlreadyTakenException,
            DataAccessException {

        AuthData authData = authenticate(authToken);
        String username = authData.username();

        if (request == null
                || request.playerColor() == null) {

            throw new BadRequestException(
                    "Missing game ID or player color"
            );
        }

        GameData existingGame = gameDAO.getGame(request.gameID());

        if (existingGame == null) {
            throw new BadRequestException(
                    "Game does not exist"
            );
        }

        GameData updatedGame;

        if (request.playerColor() == ChessGame.TeamColor.WHITE) {

            if (existingGame.whiteUsername() != null) {
                throw new AlreadyTakenException(
                        "White player is already taken"
                );
            }

            updatedGame = new GameData(
                    existingGame.gameID(),
                    username,
                    existingGame.blackUsername(),
                    existingGame.gameName(),
                    existingGame.game()
            );
        } else if (request.playerColor() == ChessGame.TeamColor.BLACK) {

            if (existingGame.blackUsername() != null) {
                throw new AlreadyTakenException(
                        "Black player is already taken"
                );
            }

            updatedGame = new GameData(
                    existingGame.gameID(),
                    existingGame.whiteUsername(),
                    username,
                    existingGame.gameName(),
                    existingGame.game()
            );
        } else {
            throw new BadRequestException(
                    "Invalid player color"
            );
        }

        gameDAO.updateGame(updatedGame);
    }
}