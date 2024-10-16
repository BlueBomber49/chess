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
        Spark.delete("/db", (req, res) -> "Database go boom");
        Spark.post("/users", (req, res) -> "Registers new user");
        Spark.post("/session", (req, res) -> "Logs in existing user, returning AuthToken");
        Spark.delete("/session", (req, res) -> "Logs out user, requires authorization");
        Spark.get("/game", (req, res) -> "Returns list of games, requires authorization");
        Spark.post("/game", (req, res) -> "Creates a new game, requires authorization");
        Spark.put("/game", (req, res) -> "Joins game, requires authorization");
        //This line initializes the server and can be removed once you have a functioning endpoint

        System.out.printf("Listening on port %d", desiredPort);
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
