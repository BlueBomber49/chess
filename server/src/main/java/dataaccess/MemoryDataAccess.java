package dataaccess;

import model.*;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
  HashMap<String, UserData> users;

  public MemoryDataAccess(){
    this.users = new HashMap<String, UserData>() ;
  }

  public void addUser(UserData person){
     users.put(person.username(), person);
  }

  public UserData getUser(String username){
    return users.get(username);
  }

  public void deleteUser(String username){
    users.remove(username);
  }

}
