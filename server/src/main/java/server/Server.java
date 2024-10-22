package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import service.*;
import service.requestClasses.LoginRequest;
import service.responseClasses.FailureMessageResponse;
import spark.*;

import java.util.ArrayList;

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
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", (req, res) -> "Returns list of games, requires authorization");
        Spark.post("/game", (req, res) -> "Creates a new game, requires authorization");
        Spark.put("/game", (req, res) -> "Joins game, requires authorization");


        Spark.awaitInitialization();
        desiredPort = Spark.port();
        System.out.printf("Listening on port %d%n", desiredPort);

        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public Object clear(Request req, Response res){
        admin.clearAll();
        res.status(200);
        return "";
    }

    public Object register(Request req, Response res){
        var person = serializer.fromJson(req.body(), UserData.class);
        AuthData token;
        String message;
        try {
            token = user.registerUser(person);
            res.status(200);
            return serializer.toJson(token);
        }
        catch (BadInputException e){
            res.status(400);
            message = "Error: Bad Request";
        }
        catch (UsernameTakenException e) {
            res.status(403);
            message = "Error: Username already taken";
        }
            return serializer.toJson(new FailureMessageResponse(message));

    }

    public Object login(Request req, Response res) throws AuthFailedException {
        var body = serializer.fromJson(req.body(), LoginRequest.class);
        var username = body.username();
        var password = body.password();
        AuthData token;
        try {
            token = auth.loginUser(username, password);
            res.status(200);
            return serializer.toJson(token);
        }
        catch (AuthFailedException e){
            res.status(401);
            var message = new FailureMessageResponse("Error: Unauthorized");
            return serializer.toJson(message);
        }
    }

    public Object logout(Request req, Response res) {
        var token = req.headers("Authorization");
        String message = "";
        try {
            auth.logoutUser(token);
        }
        catch (AuthFailedException e){
            res.status(401);
            message = "Error: Unauthorized";
            return serializer.toJson(new FailureMessageResponse(message));
        }
        return "";
    }

}
