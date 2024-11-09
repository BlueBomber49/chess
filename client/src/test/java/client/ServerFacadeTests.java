package client;

import dataaccess.DataAccess;
import dataaccess.SQLDataAccess;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;

import org.mindrot.jbcrypt.BCrypt;
import server.Server;
import ui.ServerFacade;


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
}
