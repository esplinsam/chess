package dataaccess;

import java.util.ArrayList;
import java.util.Collection;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, model.GameData> games = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public int createGame(model.GameData gameData)
            throws DataAccessException {

        int gameID = nextGameID++;

        model.GameData newGame = new model.GameData(
                gameID,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                gameData.game()
        );

        games.put(gameID, newGame);
        return gameID;
    }

    @Override
    public model.GameData getGame(int gameID)
            throws DataAccessException {
        if (!games.containsKey(gameID)) {
            return null;
        }

        return games.get(gameID);
    }

    @Override
    public Collection<model.GameData> listGames()
            throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(model.GameData gameData)
            throws DataAccessException {
        if (!games.containsKey(gameData.gameID())) {
            throw new DataAccessException(
                    "Game does not exist: " + gameData.gameID()
            );
        }

        games.put(gameData.gameID(), gameData);
    }

    @Override
    public void clear()
        throws DataAccessException {
        games.clear();
        nextGameID = 1;
    }
}