package dataaccess;

import java.util.Collection;

public class MemoryGameDAO implements GameDAO {
    @Override
    public int createGame(model.GameData gameData)
            throws DataAccessException {
        return -999;
    }

    @Override
    public model.GameData getGame(int gameID)
            throws DataAccessException {
        return null;
    }

    @Override
    public Collection<model.GameData> listGames()
            throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(model.GameData gameData)
            throws DataAccessException {}

    @Override
    public void clear()
        throws DataAccessException {}
}