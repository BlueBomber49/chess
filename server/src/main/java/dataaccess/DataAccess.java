package dataaccess;

import dataaccess.exception.DataAccessException;
import model.*;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DataAccess {
  public void addUser(UserData person) throws SQLException, DataAccessException;
  public UserData getUser(String username);
  public void deleteUser(String username);

  public void addAuth(AuthData authData);
  public AuthData getAuth(String token);
  public void deleteAuth(String token);
  public int createGame(String gameName);  //Returns game id
  public void deleteGame(int gameId);
  public void updateGame(GameData game); //Send in the new game to replace the old one
  public GameData getGame(String gameName);
  public GameData getGame(int gameId);
  public ArrayList<GameData> getAllGames();
  public void clearAll();
}
