package dataaccess;

import model.UserData;

public interface DataAccess {
  public void addUser(UserData person);
  public UserData getUser(String username);
  public void deleteUser(String username);
}
