package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exception.AuthFailedException;
import service.exception.BadInputException;
import service.exception.ColorTakenException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
  DataAccess data;
  UserData bob;
  UserData felix;
  AuthData auth;
  AuthData auth2;
  GameService service;
  @BeforeEach
  public void setup() throws ResponseException {
    data = new MemoryDataAccess();
    service = new GameService(data);
    bob = new UserData("bob", "canwefixit", "yes@wecan");
    felix = new UserData("felix", "icanfixit", "gonn@wreckit");
    data.addUser(bob);
    data.addUser(felix);
    auth = new AuthData("supahsecuretoken", "bob");
    auth2 = new AuthData("token", "felix");
    data.addAuth(auth);
    data.addAuth(auth2);
    data.createGame("Bob's game");
    data.createGame("Felix's game");
  }

  @Test
  public void successfulListGames() throws AuthFailedException, ResponseException {
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
  public void successfulCreateGame() throws AuthFailedException, ResponseException {
    service.createGame("supahsecuretoken", "new game");
    GameData expected = new GameData(3, null, null, "new game", new ChessGame());
    assertEquals(expected, data.getGame(3));
  }

  @Test
  public void failedCreateGame() throws AuthFailedException, ResponseException {
    assertThrows(AuthFailedException.class, () -> service.createGame("fake token", "null"));
    service.createGame("supahsecuretoken", "new game");
    GameData notExpected = new GameData(3, null, null, "wrong name", new ChessGame());
    assertNotEquals(notExpected, data.getGame(3));
  }

  @Test
  public void successfulJoinGame() throws AuthFailedException, BadInputException, ColorTakenException, ResponseException {
    service.joinGame("supahsecuretoken", 1, ChessGame.TeamColor.WHITE);
    GameData game1 = new GameData(1, "bob", null, "Bob's game", new ChessGame());
    assertEquals(game1, data.getGame(1));
    service.joinGame("token", 1, ChessGame.TeamColor.BLACK);
    game1 = new GameData(1, "bob", "felix", "Bob's game", new ChessGame());
    assertEquals(game1, data.getGame(1));
  }

  @Test
  public void failedJoinGame() throws AuthFailedException, BadInputException, ColorTakenException, ResponseException {
    service.joinGame("supahsecuretoken", 1, ChessGame.TeamColor.WHITE);
    assertThrows(ColorTakenException.class, () -> service.joinGame("supahsecuretoken", 1, ChessGame.TeamColor.WHITE));
    assertThrows(AuthFailedException.class, () -> service.joinGame("bad token", 1, ChessGame.TeamColor.BLACK));
    assertThrows(BadInputException.class, () -> service.joinGame("supahsecuretoken", 77, ChessGame.TeamColor.BLACK));
  }


}
