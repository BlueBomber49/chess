package dataaccess;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.SQLDataAccess;
import dataaccess.exception.ResponseException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLDataException;
import java.util.ArrayList;

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
  public void badAddUserTest() throws ResponseException {
    var badUser = new UserData("badUser", null, null);
    assertThrows(ResponseException.class, () -> data.addUser(badUser));
  }

  @Test
  public void goodDeleteUserTest() throws ResponseException {
    data.addUser(felix);
    assertEquals(felix, data.getUser(felix.username()));
    data.deleteUser("felix");
    assertNull(data.getUser(felix.username()));
  }

  @Test
  public void badDeleteUserTest() throws ResponseException {
    data.addUser(felix);
    assertDoesNotThrow(() -> data.deleteUser("misspelled"));
  }

  @Test
  public void goodAddAuthTest() throws ResponseException {
    assertNull(data.getAuth(auth.authToken()));
    data.addAuth(auth);
    assertEquals(auth, data.getAuth(auth.authToken()));
  }

  @Test
  public void badAddAuthTest() throws ResponseException {
    AuthData badAuth = new AuthData(null, "Bob");
    assertThrows(ResponseException.class, () -> data.addAuth(badAuth));
  }

  @Test
  public void goodDeleteAuthTest() throws ResponseException {
    data.addAuth(auth);
    assertEquals(auth, data.getAuth(auth.authToken()));
    data.deleteAuth(auth.authToken());
    assertNull(data.getAuth(auth.authToken()));
  }

  @Test
  public void badDeleteAuthTest() throws ResponseException {
    data.addAuth(auth);
    assertDoesNotThrow(() -> data.deleteAuth("misspelled"));
  }


  @Test
  public void goodCreateGameTest() throws ResponseException {
    assertNull(data.getGame(1));
    int id = data.createGame("Bob's game");
    GameData expected = new GameData(1, null, null, "Bob's game", new ChessGame());
    assertEquals(expected, data.getGame(id));
  }

  @Test
  public void badCreateGameTest() throws ResponseException {
    assertThrows(ResponseException.class, () -> data.createGame(null));
  }

  @Test
  public void goodDeleteGameTest() throws ResponseException {
    int id = data.createGame("Bob's game");
    GameData expected = new GameData(1, null, null, "Bob's game", new ChessGame());
    assertEquals(expected, data.getGame(id));
    data.deleteGame(id);
    assertNull(data.getGame(id));
  }

  @Test
  public void badDeleteGameTest() throws ResponseException {
    assertDoesNotThrow(() -> data.deleteGame(9999));
  }

  @Test
  public void goodUpdateGameTest() throws ResponseException {
    int id = data.createGame("Bob's game");
    GameData expected = new GameData(1, null, null, "Bob's game", new ChessGame());
    assertEquals(expected, data.getGame(id));
    GameData updated = new GameData(1, "Bob", "Felix", "Bob's game", new ChessGame());
    data.updateGame(updated);
    assertEquals(updated, data.getGame(id));
  }

  @Test
  public void badUpdateGameTest() throws ResponseException {
    int id = data.createGame("Bob's game");
    GameData expected = new GameData(1, null, null, "Bob's game", new ChessGame());
    assertEquals(expected, data.getGame(id));
    GameData badUpdated = new GameData(1, "Bob", "Felix", null, null);
    assertThrows(ResponseException.class, () -> data.updateGame(badUpdated));
  }

  @Test
  public void goodGetGameTest() throws ResponseException {
    int id = data.createGame("Bob's game");
    GameData expected = new GameData(1, null, null, "Bob's game", new ChessGame());
    assertEquals(expected, data.getGame(id));
    assertEquals(data.getGame(id), data.getGame("Bob's game"));
  }

  @Test
  public void badGetGameTest() throws ResponseException {
    int id = data.createGame("Bob's game");
    assertNull(data.getGame(null));
  }

  @Test
  public void getAllGamesTest() throws ResponseException{
    int id = data.createGame("Bob's game");
    int id2 = data.createGame("Felix's game");
    GameData expected = new GameData(1, null, null, "Bob's game", new ChessGame());
    GameData expected2 = new GameData(2, null, null, "Felix's game", new ChessGame());
    var list = new ArrayList<GameData>();
    list.add(expected);
    list.add(expected2);
    assertEquals(list, data.getAllGames());
  }


}
