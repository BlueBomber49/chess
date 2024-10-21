package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.*;
import spark.*;

public class Server {
    private AdminService admin;
    private AuthService auth;
    private GameService game;
    private UserService user;
    private DataAccess data;
    private Gson serializer = new Gson();

    public Server(){
        data = new MemoryDataAccess();
        this.admin = new AdminService(data);
        this.auth = new AuthService(data);
        this.game = new GameService(data);
        this.user = new UserService(data);
    }

    public int run(int desiredPort) {
         Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (req, res) -> clear());
        Spark.post("/users", (req, res) -> "Registers user");
        Spark.post("/session", (req, res) -> "Logs in existing user, returning AuthToken");
        Spark.delete("/session", (req, res) -> "Logs out user, requires authorization");
        Spark.get("/game", (req, res) -> "Returns list of games, requires authorization");
        Spark.post("/game", (req, res) -> "Creates a new game, requires authorization");
        Spark.put("/game", (req, res) -> "Joins game, requires authorization");

        int x = Spark.port();
        System.out.printf("Listening on port %d", x);
        return x;
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public Object clear(){
        System.out.println("Hit the clear function");
        admin.clearAll();
        return serializer.toJson("Success");
    }

    public Object register(Request req){
        return null;
    }

}
