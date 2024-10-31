package dataaccess;
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;
import java.util.ArrayList;
import dataaccess.exception.*;
import com.google.gson.Gson;

public class SQLDataAccess implements DataAccess{
  private Gson serializer = new Gson();

  public SQLDataAccess() throws ResponseException {
    try {
      configureDatabase();
    }
    catch(Exception e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
  }


  void configureDatabase() throws ResponseException {
    try {
      DatabaseManager.createDatabase();
      try (var conn=DatabaseManager.getConnection()) {


        //Add create tables statements
        var createUserTableStatement="""
                CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY(username)
                )""";
        var createAuthTableStatement="""
                CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL
                )""";
        var createGameTableStatement="""
                CREATE TABLE IF NOT EXISTS games (
                gameID INT NOT NULL AUTO_INCREMENT,
                whiteUsername VARCHAR(255) DEFAULT NULL,
                blackUsername VARCHAR(255) DEFAULT NULL,
                gameName VARCHAR(255) NOT NULL,
                chessGame TEXT NOT NULL,
                PRIMARY KEY(gameID)
                )
                """;
        var preparedStatement1=conn.prepareStatement(createUserTableStatement);
        preparedStatement1.executeUpdate();
        preparedStatement1.close();
        var preparedStatement2=conn.prepareStatement(createAuthTableStatement);
        preparedStatement2.executeUpdate();
        preparedStatement2.close();
        var preparedStatement3=conn.prepareStatement(createGameTableStatement);
        preparedStatement3.executeUpdate();
        preparedStatement3.close();
        System.out.println("Successful initialization of database");
      }
    }
    catch(Exception e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
  }

  @Override
  public void addUser(UserData person) throws ResponseException {
    try(var conn = DatabaseManager.getConnection()) {
      var username=person.username();
      var password=person.password();
      var email=person.email();
      var preparedStatement=conn.prepareStatement("INSERT INTO users (username, password, email) VALUES(?,?,?)");
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, email);
        preparedStatement.executeUpdate();

    }
    catch(DataAccessException e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
    catch(SQLException e){
      throw new ResponseException(400, "Error: Invalid value given");
    }
  }

  @Override
  public UserData getUser(String username) throws ResponseException {
    try(var conn = DatabaseManager.getConnection()) {
      var prepped = conn.prepareStatement("SELECT * FROM users WHERE username = ?;");
      prepped.setString(1, username);
      var result = prepped.executeQuery();
      if(result.next()){
        return new UserData(result.getString("username"), result.getString("password"), result.getString("email"));
      }
    }
    catch(DataAccessException e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
    catch(SQLException e){
      throw new ResponseException(400, "Error: Invalid value given");
    }
    return null;
  }

  @Override
  public void deleteUser(String username) throws ResponseException {
    try(var conn = DatabaseManager.getConnection()) {
      var prepped=conn.prepareStatement("DELETE FROM users WHERE username = ?;");
      prepped.setString(1, username);
      prepped.executeUpdate();
    }
    catch(DataAccessException e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
    catch(SQLException e){
      throw new ResponseException(400, "Error: Invalid value given");
    }
  }

  @Override
  public void addAuth(AuthData authData) throws ResponseException {
    try(var conn = DatabaseManager.getConnection()){
      var token = authData.authToken();
      var username = authData.username();
      var preparedStatement = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES(?,?);");
      preparedStatement.setString(1, token);
      preparedStatement.setString(2, username);
      preparedStatement.executeUpdate();
    }
    catch(DataAccessException e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
    catch(SQLException e){
      throw new ResponseException(400, "Error: Invalid value given");
    }
  }

  @Override
  public AuthData getAuth(String token) throws ResponseException {
    try(var conn = DatabaseManager.getConnection()) {
      var prepped = conn.prepareStatement("SELECT * FROM auth WHERE authToken = ?;");
      prepped.setString(1, token);
      var result = prepped.executeQuery();
      if(result.next()){
        return new AuthData(result.getString("authToken"), result.getString("username"));
      }
    }
    catch(DataAccessException e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
    catch(SQLException e){
      throw new ResponseException(400, "Error: Invalid value given");
    }
    return null;
  }

  @Override
  public void deleteAuth(String token) throws ResponseException {
    try(var conn = DatabaseManager.getConnection()) {
      var prepped=conn.prepareStatement("DELETE FROM auth WHERE authToken = ?;");
      prepped.setString(1, token);
      prepped.executeUpdate();
    }
    catch(DataAccessException e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
    catch(SQLException e){
      throw new ResponseException(400, "Error: Invalid value given");
    }

  }

  @Override
  public int createGame(String gameName) throws ResponseException {
    try(var conn = DatabaseManager.getConnection()){
      var prepped = conn.prepareStatement("INSERT INTO games (gameName, chessGame) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
      ChessGame game = new ChessGame();
      prepped.setString(1, gameName);
      prepped.setString(2, serializer.toJson(game));
      prepped.executeUpdate();
      var rs = prepped.getGeneratedKeys();
      rs.next();
      int id = rs.getInt(1);
      return id;
    }
    catch(DataAccessException e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
    catch(SQLException e){
      throw new ResponseException(400, "Error: Invalid value given");
    }
  }

  @Override
  public void deleteGame(int gameId) throws ResponseException {
    try(var conn = DatabaseManager.getConnection()) {
      var prepped=conn.prepareStatement("DELETE FROM games WHERE gameID = ?;");
      prepped.setInt(1, gameId);
      prepped.executeUpdate();
    }
    catch(DataAccessException e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
    catch(SQLException e){
      throw new ResponseException(400, "Error: Invalid value given");
    }
  }

  @Override
  public void updateGame(GameData game) throws ResponseException {
    try(var conn = DatabaseManager.getConnection()) {
      var whiteUsername = game.whiteUsername();
      var blackUsername = game.blackUsername();
      var gameName = game.gameName();
      var chessGame = game.game();
      var gameId = game.gameId();
      var prepped=conn.prepareStatement("UPDATE games " +
              "SET whiteUsername = ?, blackUsername = ?, gameName = ?, " +
              "chessGame = ? WHERE gameID = ?;");
      prepped.setString(1, whiteUsername);
      prepped.setString(2, blackUsername);
      prepped.setString(3, gameName);
      prepped.setString(4, serializer.toJson(chessGame));
      prepped.setInt(5, gameId);
      prepped.executeUpdate();
    }
    catch(DataAccessException e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
    catch(SQLException e){
      throw new ResponseException(400, "Error: Invalid value given");
    }
  }

  @Override
  public GameData getGame(String gameName) throws ResponseException {
    try(var conn = DatabaseManager.getConnection()) {
      var prepped = conn.prepareStatement("SELECT * FROM games WHERE gameName = ?;");
      prepped.setString(1, gameName);
      var result = prepped.executeQuery();
      if(result.next()){
        int gameID = result.getInt("gameID");
        String whiteUsername = result.getString("whiteUsername");
        String blackUsername = result.getString("blackUsername");
        String resultGameName = result.getString("gameName");
        String gameJson = result.getString("chessGame");
        ChessGame game = serializer.fromJson(gameJson, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, resultGameName, game);
      }
    }
    catch(DataAccessException e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
    catch(SQLException e){
      throw new ResponseException(400, "Error: Invalid value given");
    }
    return null;
  }

  @Override
  public GameData getGame(int gameId) throws ResponseException {
    try(var conn = DatabaseManager.getConnection()) {
      var prepped = conn.prepareStatement("SELECT * FROM games WHERE gameID = ?;");
      prepped.setInt(1, gameId);
      var result = prepped.executeQuery();
      if(result.next()){
        int gameID = result.getInt("gameID");
        String whiteUsername = result.getString("whiteUsername");
        String blackUsername = result.getString("blackUsername");
        String resultGameName = result.getString("gameName");
        String gameJson = result.getString("chessGame");
        ChessGame game = serializer.fromJson(gameJson, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, resultGameName, game);
      }
    }
    catch(DataAccessException e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
    catch(SQLException e){
      throw new ResponseException(400, "Error: Invalid value given");
    }
    return null;
  }

  @Override
  public ArrayList<GameData> getAllGames() throws ResponseException {
    try(var conn = DatabaseManager.getConnection()) {
      var prepped = conn.prepareStatement("SELECT * FROM games;");
      var result = prepped.executeQuery();
      var gameList = new ArrayList<GameData>();
      while(result.next()){
        int gameID = result.getInt("gameID");
        String whiteUsername = result.getString("whiteUsername");
        String blackUsername = result.getString("blackUsername");
        String resultGameName = result.getString("gameName");
        String gameJson = result.getString("chessGame");
        ChessGame game = serializer.fromJson(gameJson, ChessGame.class);
        var data = new GameData(gameID, whiteUsername, blackUsername, resultGameName, game);
        gameList.add(data);
      }
      return gameList;
    }
    catch(DataAccessException e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
    catch(SQLException e){
      throw new ResponseException(400, "Error: Invalid value given");
    }
  }

  @Override
  public void clearAll() throws ResponseException {
    try(var conn = DatabaseManager.getConnection()){
      var preparedStatement1 = conn.prepareStatement("TRUNCATE TABLE users");
      var preparedStatement2 = conn.prepareStatement("TRUNCATE TABLE auth");
      var preparedStatement3 = conn.prepareStatement("TRUNCATE TABLE games");
      preparedStatement1.executeUpdate();
      preparedStatement2.executeUpdate();
      preparedStatement3.executeUpdate();
    }
    catch(Exception e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
  }

 }