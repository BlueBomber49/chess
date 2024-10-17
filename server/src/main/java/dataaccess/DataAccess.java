package dataaccess;

import model.*;

public interface DataAccess {
  public void addUser(UserData person);
  public UserData getUser(String username);
  public void deleteUser(String username);
  public void addAuth(AuthData authData);
  public AuthData getAuth(String token);
  public void deleteAuth(String token);
}
