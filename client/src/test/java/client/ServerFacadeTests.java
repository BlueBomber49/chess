package client;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.SQLDataAccess;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;

import org.mindrot.jbcrypt.BCrypt;
import responseclasses.GameResponse;
import server.Server;
import ui.ServerFacade;

import java.util.ArrayList;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static String user;
    private static String password;
    private static String email;
    private static DataAccess data;
    private static String existingUser;
    private static String existingPassword;
    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        data = new SQLDataAccess();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
        user = "Bob";
        password = "bobbob";
        email = "bob@bob.bob";
        existingUser = "Felix";
        existingPassword = "icanfixit";
    }

    @BeforeEach
    public void setup(){
        try {
            data.clearAll();
            data.addUser(new UserData("Felix", BCrypt.hashpw("icanfixit", BCrypt.gensalt()), "mrfixit@email"));
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    @AfterAll
    static void stopServer() throws ResponseException {
        data.clearAll();
        server.stop();
    }

    @Test
    public void goodRegisterTest() throws ResponseException {
    facade.register(user, password, email);
    var person = data.getUser(user);
    Assertions.assertEquals(person.username(), user);
    Assertions.assertTrue(BCrypt.checkpw(password, person.password()));
    }

    @Test
    public void badRegisterTest() throws ResponseException{
        Assertions.assertThrows(ResponseException.class, () -> facade.register(user, null, null));
    }

    @Test
    public void goodLoginTest() throws ResponseException{
        AuthData auth = facade.login(existingUser, existingPassword);
        Assertions.assertEquals(auth, data.getAuth(auth.authToken()));
    }

    @Test
    public void badLoginTest() throws ResponseException{
        Assertions.assertThrows(ResponseException.class, () -> facade.login(user, password));
    }

    @Test
    public void goodLogoutTest() throws ResponseException{
        var auth = facade.login("Felix", "icanfixit");
        facade.logout(auth.authToken());
        Assertions.assertNull(data.getAuth("Felix"));
    }

    @Test
    public void badLogoutTest() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> facade.logout("Not an auth token"));
    }

    @Test
    public void goodListGamesTest() throws ResponseException {
        data.createGame("Bob's game");
        var game1 = new GameResponse(1, null, null, "Bob's game");
        var game2 = new GameResponse(2, null, null, "Felix's game");
        data.createGame("Felix's game");
        var gameList = new ArrayList<GameResponse> ();
        gameList.add(game1);
        gameList.add(game2);
        var auth = facade.register(user, password, email);
        Assertions.assertEquals(gameList, facade.listGames(auth.authToken()));
    }

    @Test
    public void badListGamesTest() throws ResponseException {
        var auth = facade.register(user, password, email);
        Assertions.assertEquals(new ArrayList<GameResponse>(), facade.listGames(auth.authToken()));
    }

    @Test
    public void goodCreateGameTest() throws ResponseException {
        var auth = facade.register(user, password, email);
        var id = facade.createGame(auth.authToken(), "Bob's game").gameID();
        var game = new GameData(id, null, null, "Bob's game", new ChessGame());
        Assertions.assertEquals(game, data.getGame(id));
    }

    @Test
    public void badCreateGameTest() throws ResponseException {
        var auth = facade.register(user, password, email);
        Assertions.assertThrows(ResponseException.class, () ->facade.createGame(auth.authToken(), null));
    }

    @Test
    public void goodJoinGameTest() throws ResponseException {
        var auth = facade.register(user, password, email);
        var id = data.createGame("Bob's game");
        facade.joinGame(auth.authToken(), id, ChessGame.TeamColor.WHITE);
        var game = new GameData(id, "Bob", null, "Bob's game", new ChessGame());
        Assertions.assertEquals(game, data.getGame(id));
    }

    @Test
    public void badJoinGameTest() throws ResponseException{
        var auth = facade.register(user, password, email);
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(auth.authToken(), -1, ChessGame.TeamColor.WHITE));
    }
}
