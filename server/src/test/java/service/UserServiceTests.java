package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
  public void registerFailureTest() throws BadInputException, UsernameTakenException {
    bob = new UserData("bob", null, "");
    assertThrows(BadInputException.class, () -> service.registerUser(bob));
    service.registerUser(felix);
    assertThrows(UsernameTakenException.class, () -> service.registerUser(felix));
  }

  @Test
  public void registerSuccessfulTest() throws BadInputException, UsernameTakenException {
    AuthData id = service.registerUser(felix);
    assertEquals(felix, data.getUser(felix.username()));
    assertEquals(id, data.getAuth(id.authToken()));
  }
}
