package dataaccess;

import dataaccess.exception.DataAccessException;
import dataaccess.exception.ResponseException;
import model.*;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DataAccess {
  public void addUser(UserData person) throws ResponseException;
  public UserData getUser(String username) throws ResponseException;
  public void deleteUser(String username) throws ResponseException;

  public void addAuth(AuthData authData) throws ResponseException;
  public AuthData getAuth(String token) throws ResponseException;
  public void deleteAuth(String token) throws ResponseException;
  public int createGame(String gameName);  //Returns game id
  public void deleteGame(int gameId);
  public void updateGame(GameData game); //Send in the new game to replace the old one
  public GameData getGame(String gameName);
  public GameData getGame(int gameId);
  public ArrayList<GameData> getAllGames();
  public void clearAll() throws ResponseException;
}
