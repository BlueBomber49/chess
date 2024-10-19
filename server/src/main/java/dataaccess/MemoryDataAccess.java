package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
  int gameID = 0;
  HashMap<String, UserData> users; //Username as key
  HashMap<String, AuthData> auth;  //AuthToken as key
  HashMap<Integer, GameData> games;  //GameID as key

  public MemoryDataAccess(){
    this.users = new HashMap<String, UserData>();
    this.auth = new HashMap<String, AuthData>();
    this.games = new HashMap<Integer, GameData>();
  }

  /*public What gets returned? getAllUsers(){
    return users;
  }
  */


  public void clearAll(){
    users.clear();
    auth.clear();
    games.clear();
  }

  // UserData DAO
  public void addUser(UserData person){
     users.put(person.username(), person);
  }

  public UserData getUser(String username){
    return users.get(username);
  }

  public void deleteUser(String username){
    users.remove(username);
  }


  //AuthData DAO
  public void addAuth(AuthData authorization){
    auth.put(authorization.authToken(), authorization);
  }

  public void deleteAuth(String token){
    auth.remove(token);
  }

  public AuthData getAuth(String token){
    return auth.get(token);
  }

// Game DAO
  public int createGame(String gameName){
    gameID++;
    ChessGame game = new ChessGame();
    GameData gameObject = new GameData(gameID, null, null, gameName, game);
    games.put(gameID, gameObject);
    return gameID;
  }

  public void deleteGame(int gameId) {
    games.remove(gameId);
  }

  public void updateGame(GameData game) {
    games.put(game.gameId(), game);
  }

  public GameData getGame(String gameName) {
    for(GameData game: games.values()){
      if(game.gameName() == gameName){
        return game;
      }
    }
    return null;
  }

  public GameData getGame(int gameId) {
    return games.get(gameId);
  }

  
  public ArrayList<GameData> getAllGames() {
    ArrayList<GameData> allGames = new ArrayList<>();
    for(Map.Entry<Integer, GameData> set : games.entrySet()){
      allGames.add(set.getValue());
    }
    return allGames;
  }

}
