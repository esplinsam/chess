package dataaccess;

import java.util.Collection;

public interface GameDAO {
    int createGame(model.GameData game) throws DataAccessException;

    model.GameData getGame(int gameID) throws DataAccessException;

    Collection<model.GameData> listGames() throws DataAccessException;

    void updateGame(model.GameData game) throws DataAccessException;

    void clear() throws DataAccessException;

}