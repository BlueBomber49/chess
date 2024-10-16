package server;

import service.*;
import spark.*;

public class Server {
    private AdminService admin;
    private AuthService auth;
    private GameService game;
    private UserService user;

    public Server(){
        this.admin = new AdminService();
        this.auth = new AuthService();
        this.game = new GameService();
        this.user = new UserService();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.get("/users", (req, res) -> "It works!");
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        System.out.printf("Listening on port %d", desiredPort);
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
