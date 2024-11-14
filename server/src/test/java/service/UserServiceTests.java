package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import exception.BadInputException;
import exception.UsernameTakenException;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
  DataAccess data;
  UserData bob;
  UserData felix;
  AuthData auth;
  UserService service;
  @BeforeEach
  public void setup(){
    data = new MemoryDataAccess();
    service = new UserService(data);
    bob = new UserData("bob", "canwefixit", "");
    felix = new UserData("felix", "icanfixit", "gonn@wreckit");
    auth = new AuthData("supahsecuretoken", "felix");
  }

  @Test
  public void registerFailureTest() throws BadInputException, UsernameTakenException, ResponseException {
    bob = new UserData("bob", null, "");
    assertThrows(BadInputException.class, () -> service.registerUser(bob));
    service.registerUser(felix);
    assertThrows(UsernameTakenException.class, () -> service.registerUser(felix));
  }

  @Test
  public void registerSuccessfulTest() throws BadInputException, UsernameTakenException, ResponseException {
    AuthData id = service.registerUser(felix);
    assertTrue(BCrypt.checkpw(felix.password(), data.getUser(felix.username()).password()));
    assertEquals(id, data.getAuth(id.authToken()));
  }
}
