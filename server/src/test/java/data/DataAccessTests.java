package data;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.SQLDataAccess;
import dataaccess.exception.ResponseException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLDataException;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {

  DataAccess data;
  UserData bob;
  UserData felix;
  AuthData auth;
  GameData game;
  @BeforeEach
  public void setup() throws ResponseException {
    data = new SQLDataAccess();
    data.clearAll();
    bob = new UserData("bob", "canwefixit", "yes@wecan");
    felix = new UserData("felix", "icanfixit", "gonn@wreckit");
    auth = new AuthData("supahsecuretoken", "bob");
  }

  @Test
  public void goodAddUserTest() throws ResponseException {
    assertNull(data.getUser(bob.username()));
    data.addUser(bob);
    assertEquals(bob, data.getUser(bob.username()));
  }

  @Test
  public void badAddUserTest(){
    var badUser = new UserData("badUser", null, null);

  }

  @Test
  public void deleteUserTest() throws ResponseException {
    data.addUser(felix);
    assertEquals(felix, data.getUser(felix.username()));
    data.deleteUser("felix");
    assertNull(data.getUser(felix.username()));
  }

  @Test
  public void addAuthTest() throws ResponseException {
    assertNull(data.getAuth(auth.authToken()));
    data.addAuth(auth);
    assertEquals(auth, data.getAuth(auth.authToken()));
  }

  @Test
  public void deleteAuthTest() throws ResponseException {
    data.addAuth(auth);
    assertEquals(auth, data.getAuth(auth.authToken()));
    data.deleteAuth(auth.authToken());
    assertNull(data.getAuth(auth.authToken()));
  }


  @Test
  public void createGameTest(){
    assertNull(data.getGame(1));
    int id = data.createGame("Bob's game");
    GameData expected = new GameData(1, null, null, "Bob's game", new ChessGame());
    assertEquals(expected, data.getGame(id));
  }

  @Test
  public void deleteGameTest(){
    int id = data.createGame("Bob's game");
    GameData expected = new GameData(1, null, null, "Bob's game", new ChessGame());
    assertEquals(expected, data.getGame(id));
    data.deleteGame(id);
    assertNull(data.getGame(id));
  }

  @Test
  public void updateGameTest(){
    int id = data.createGame("Bob's game");
    GameData expected = new GameData(1, null, null, "Bob's game", new ChessGame());
    assertEquals(expected, data.getGame(id));
    GameData updated = new GameData(1, "Bob", "Felix", "Bob's game", new ChessGame());
    data.updateGame(updated);
    assertEquals(updated, data.getGame(id));
  }

  @Test
  public void getGameByNameTest(){
    int id = data.createGame("Bob's game");
    GameData expected = new GameData(1, null, null, "Bob's game", new ChessGame());
    assertEquals(expected, data.getGame(id));
    assertEquals(data.getGame(id), data.getGame("Bob's game"));
  }


}
