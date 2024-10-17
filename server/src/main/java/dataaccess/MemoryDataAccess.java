package dataaccess;

import model.*;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
  HashMap<String, UserData> users;
  HashMap<String, AuthData> auth;
  HashMap<String, GameData> games;

  public MemoryDataAccess(){
    this.users = new HashMap<String, UserData>();
    this.auth = new HashMap<String, AuthData>();
    this.games = new HashMap<String, GameData>();
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

}
