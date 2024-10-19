package service;

import dataaccess.DataAccess;
import model.*;

import java.util.UUID;

public class AuthService {
  private DataAccess data;
  public AuthService(DataAccess data){
    this.data = data;

  }

  public AuthData loginUser(String username, String password) throws AuthFailedException {
    UserData user = data.getUser(username);
    if(user != null){
      if(user.password() == password){
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        AuthData auth = new AuthData(id, username);
        data.addAuth(auth);
        return auth;
      }
      throw new AuthFailedException("Username or password incorrect");
    }
    throw new AuthFailedException("Username or password incorrect");
  }

  public void logoutUser(String authToken) throws AuthFailedException {
    AuthData auth = data.getAuth(authToken);
    if(auth != null){
      data.deleteAuth(authToken);
    }
    else{
      throw new AuthFailedException("Unauthorized");
    }
  }

}
