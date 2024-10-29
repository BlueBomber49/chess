package service;

import dataaccess.DataAccess;
import dataaccess.exception.ResponseException;
import model.*;
import service.exception.BadInputException;
import service.exception.UsernameTakenException;

import java.util.UUID;

public class UserService {
  private DataAccess data;
  public UserService(DataAccess data){
    this.data = data;
  }

  public AuthData registerUser(UserData user) throws BadInputException, UsernameTakenException, ResponseException {
    if (user.password() != null && user.username() != null && user.email() != null) {
      if (data.getUser(user.username()) == null) {
        data.addUser(user);
        UUID uuid=UUID.randomUUID();
        String id=uuid.toString();
        AuthData auth=new AuthData(id, user.username());
        data.addAuth(auth);
        return auth;
      } else {
        throw new UsernameTakenException("Username already taken");
      }
    }
    else{
      throw new BadInputException("Inputs cannot be null");
    }
  }



}
