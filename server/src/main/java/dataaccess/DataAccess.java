package dataaccess;

import model.*;

public interface DataAccess {
  public void addUser(UserData person);
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
}
