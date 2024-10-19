package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
  DataAccess data;
  UserData bob;
  UserData felix;
  AuthData auth;
  GameService service;
  @BeforeEach
  public void setup(){
    data = new MemoryDataAccess();
    service = new GameService(data);
    bob = new UserData("bob", "canwefixit", "yes@wecan");
    felix = new UserData("felix", "icanfixit", "gonn@wreckit");
    data.addUser(bob);
    data.addUser(felix);
    auth = new AuthData("supahsecuretoken", "bob");
    data.addAuth(auth);
    data.createGame("Bob's game");
    data.createGame("Felix's game");
  }

  @Test
  public void successfulListGames() throws AuthFailedException {
    ArrayList<GameData> list = new ArrayList<>();
    GameData game1 = new GameData(1, null, null, "Bob's game", new ChessGame());
    GameData game2 = new GameData(2, null, null, "Felix's game", new ChessGame());
    list.add(game1);
    list.add(game2);
    assertEquals(list, service.listGames(auth.authToken()));
  }


  @Test
  public void failedListGames(){
    assertThrows(AuthFailedException.class, () -> service.listGames("not a token"));
  }

  @Test
  public void successfulCreateGame() throws AuthFailedException {
    service.createGame("supahsecuretoken", "new game");
    GameData expected = new GameData(3, null, null, "new game", new ChessGame());
    assertEquals(expected, data.getGame("new game"));
  }

  @Test
  public void failedCreateGame() throws AuthFailedException{
    assertThrows(AuthFailedException.class, () -> service.createGame("fake token", "null"));
    service.createGame("supahsecuretoken", "new game");
    GameData notExpected = new GameData(3, null, null, "wrong name", new ChessGame());
    assertNotEquals(notExpected, data.getGame("new game"));
  }

  @Test
  public void successfulJoinGame() throws AuthFailedException, BadInputException {
    service.joinGame("supahsecuretoken", 1, "Bob", ChessGame.TeamColor.WHITE);
    GameData game1 = new GameData(1, "Bob", null, "Bob's game", new ChessGame());
    assertEquals(game1, data.getGame("Bob's game"));
    service.joinGame("supahsecuretoken", 1, "Felix", ChessGame.TeamColor.BLACK);
    game1 = new GameData(1, "Bob", "Felix", "Bob's game", new ChessGame());
    assertEquals(game1, data.getGame("Bob's game"));
  }

  @Test
  public void failedJoinGame() throws AuthFailedException, BadInputException {
    service.joinGame("supahsecuretoken", 1, "Bob", ChessGame.TeamColor.WHITE);
    assertThrows(BadInputException.class, () -> service.joinGame("supahsecuretoken", 1, "Other Bob", ChessGame.TeamColor.WHITE));
    assertThrows(AuthFailedException.class, () -> service.joinGame("bad token", 1, "Gene", ChessGame.TeamColor.WHITE));
    assertThrows(BadInputException.class, () -> service.joinGame("supahsecuretoken", 77, "hacker", ChessGame.TeamColor.WHITE));
  }


}
