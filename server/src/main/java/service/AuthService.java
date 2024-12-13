package service;

import dataaccess.DataAccess;
import exception.ResponseException;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import exception.AuthFailedException;

import java.util.UUID;

public class AuthService {
  private DataAccess data;
  public AuthService(DataAccess data){
    this.data = data;

  }

  public AuthData loginUser(String username, String password) throws AuthFailedException, ResponseException {
    UserData user = data.getUser(username);
    if(user != null){
      if(BCrypt.checkpw(password, user.password())){
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

  public void logoutUser(String authToken) throws AuthFailedException, ResponseException {
    verifyAuth(authToken);
    data.deleteAuth(authToken);
  }

  public void verifyAuth(String authToken) throws AuthFailedException, ResponseException {
    if(data.getAuth(authToken) == null){
      throw new AuthFailedException("Unauthorized");
    }
  }

}
