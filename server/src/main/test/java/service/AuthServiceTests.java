package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTests {
  DataAccess data;
  UserData bob;
  UserData felix;
  AuthData auth;
  AuthService service;
  @BeforeEach
  public void setup(){
    data = new MemoryDataAccess();
    service = new AuthService(data);
    bob = new UserData("bob", "canwefixit", "yes@wecan");
    felix = new UserData("felix", "icanfixit", "gonn@wreckit");
    auth = new AuthData("supahsecuretoken", "felix");
    data.addUser(bob);
    data.addAuth(auth);
  }

  @Test
  public void loginFailTest() throws AuthFailedException{
    assertThrows(AuthFailedException.class, () -> service.loginUser("james", "canwefixit"));
    assertThrows(AuthFailedException.class, () -> service.loginUser("bob", "wrong password"));
  }

  @Test
  public void loginSuccessfulTest() throws AuthFailedException{
    AuthData id = service.loginUser("bob", "canwefixit");
    assertEquals(id, data.getAuth(id.authToken()));
  }

  @Test
  public void logoutFailTest() throws AuthFailedException{
    assertThrows(AuthFailedException.class, () -> service.logoutUser("Not an access token"));
    service.logoutUser(auth.authToken());
    assertThrows(AuthFailedException.class, () -> service.logoutUser(auth.authToken()));
  }

  @Test
  public void logoutSuccessfulTest() throws AuthFailedException{
    service.logoutUser(auth.authToken());
    assertNull(data.getAuth(auth.authToken()));
  }

}
