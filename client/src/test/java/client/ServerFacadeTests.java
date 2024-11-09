package client;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;

import server.Server;
import ui.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static String user;
    private static String password;
    private static String email;
    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
        user = "Bob";
        password = "bobbob";
        email = "bob@bob.bob";
    }

    @BeforeEach
    public void setup(){
        try {
            facade.clear();
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerTest() throws ResponseException {
        facade.register(user, password, email);
        Assertions.assertThrows(ResponseException.class, () -> facade.register(user, password, email));
    }
}
