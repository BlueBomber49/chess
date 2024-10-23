package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdminServiceTests {
  DataAccess data;
  AdminService admin;
  UserData bob;
  UserData felix;
  AuthData auth;
  @BeforeEach
  public void setup(){
    data = new MemoryDataAccess();
    admin = new AdminService(data);
    bob = new UserData("bob", "canwefixit", "yes@wecan");
    felix = new UserData("felix", "icanfixit", "gonn@wreckit");
    auth = new AuthData("supahsecuretoken", "bob");
    data.createGame("Bob's game");
    data.addAuth(auth);
    data.addUser(bob);
    data.addUser(bob);
  }

  @Test
  public void clearTest(){
    data.clearAll();
    assertNull(data.getGame("Bob's game"));
    assertNull(data.getAuth("supahsecuretoken"));
    assertNull(data.getUser(felix.username()));
    assertNull(data.getUser(bob.username()));
  }


}
