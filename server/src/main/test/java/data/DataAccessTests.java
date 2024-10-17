import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {

  DataAccess data;
  UserData bob;
  UserData felix;
  AuthData auth;
  @BeforeEach
  public void setup(){
    data = new MemoryDataAccess();
    bob = new UserData("bob", "canwefixit", "yes@wecan");
    felix = new UserData("felix", "icanfixit", "gonn@wreckit");
    auth = new AuthData("supahsecuretoken", "bob");
  }

  @Test
  public void addUserTest(){
    assertNull(data.getUser(bob.username()));
    data.addUser(bob);
    assertEquals(bob, data.getUser(bob.username()));
  }

  @Test
  public void deleteUserTest(){
    data.addUser(felix);
    assertEquals(felix, data.getUser(felix.username()));
    data.deleteUser("felix");
    assertNull(data.getUser(felix.username()));
  }

  @Test
  public void addAuthTest(){
    assertNull(data.getAuth(auth.authToken()));
    data.addAuth(auth);
    assertEquals(auth, data.getAuth(auth.authToken()));
  }

  @Test
  public void deleteAuthTest(){
    data.addAuth(auth);
    assertEquals(auth, data.getAuth(auth.authToken()));
    data.deleteAuth(auth.authToken());
    assertNull(data.getAuth(auth.authToken()));
  }


}
