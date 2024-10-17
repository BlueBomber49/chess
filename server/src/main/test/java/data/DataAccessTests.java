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
  @BeforeEach
  public void setup(){
    data = new MemoryDataAccess();
    bob = new UserData("bob", "canwefixit", "yes@wecan");
    felix = new UserData("felix", "icanfixit", "im@gonnawreckit");
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


}
