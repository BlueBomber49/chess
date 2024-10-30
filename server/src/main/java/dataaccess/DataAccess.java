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
  public int createGame(String gameName) throws ResponseException;  //Returns game id
  public void deleteGame(int gameId) throws ResponseException;
  public void updateGame(GameData game) throws ResponseException; //Send in the new game to replace the old one
  public GameData getGame(String gameName) throws ResponseException;
  public GameData getGame(int gameId) throws ResponseException;
  public ArrayList<GameData> getAllGames() throws ResponseException;
  public void clearAll() throws ResponseException;
}
