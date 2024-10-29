package dataaccess;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;
import java.util.ArrayList;
import dataaccess.exception.*;

public class SQLDataAccess implements DataAccess{

  public SQLDataAccess() throws ResponseException {
    try {
      configureDatabase();
    }
    catch(Exception e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
  }
  Connection getConnection() throws DataAccessException, SQLException {
      return DatabaseManager.getConnection();
  }

  void configureDatabase() throws ResponseException {
    try (var conn = getConnection()){
      var createDBStatement = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS chess");
      createDBStatement.executeUpdate();
      conn.setCatalog("chess");

      //Add create tables statements
      var createUserTableStatement = """
              CREATE TABLE IF NOT EXISTS users (
              username VARCHAR(255) NOT NULL,
              password VARCHAR(255) NOT NULL,
              email VARCHAR(255) NOT NULL,
              PRIMARY KEY(username)
              )""";
      var createAuthTableStatement = """
              CREATE TABLE IF NOT EXISTS auth (
              authToken VARCHAR(30) NOT NULL,
              username VARCHAR(255) NOT NULL
              )""";
      var createGameTableStatement = """
              CREATE TABLE IF NOT EXISTS games (
              gameID INT NOT NULL AUTO_INCREMENT,
              whiteUsername VARCHAR(255),
              blackUsername VARCHAR(255),
              gameName VARCHAR(255) NOT NULL,
              chessGame TEXT NOT NULL,
              PRIMARY KEY(gameID)
              )
              """;
      var preparedStatement1 = conn.prepareStatement(createUserTableStatement);
      preparedStatement1.executeUpdate();
      preparedStatement1.close();
      var preparedStatement2 = conn.prepareStatement(createAuthTableStatement);
      preparedStatement2.executeUpdate();
      preparedStatement2.close();
      var preparedStatement3 = conn.prepareStatement(createGameTableStatement);
      preparedStatement3.executeUpdate();
      preparedStatement3.close();
      System.out.println("Successful initialization of database");
    }
    catch(Exception e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
  }

  @Override
  public void addUser(UserData person) throws ResponseException {
    try(var conn = getConnection()) {
      var username=person.username();
      var password=person.password();
      var email=person.email();
      var preparedStatement=conn.prepareStatement("INSERT INTO users (username, password, email) VALUES(?,?,?)");
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, email);
        preparedStatement.executeUpdate();

    }
    catch(Exception e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
  }

  @Override
  public UserData getUser(String username) throws ResponseException {
    try(var conn = getConnection()) {
      var prepped = conn.prepareStatement("SELECT * FROM users WHERE username = ?;");
      prepped.setString(1, username);
      var result = prepped.executeQuery();
      if(result.next()){
        return new UserData(result.getString("username"), result.getString("password"), result.getString("email"));
      }
    }
    catch(Exception e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
    return null;
  }

  @Override
  public void deleteUser(String username) throws ResponseException {
    try(var conn = getConnection()) {
      var prepped=conn.prepareStatement("DELETE FROM users WHERE username = ?;");
      prepped.setString(1, username);
      prepped.executeUpdate();
    }
    catch(Exception e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
  }

  @Override
  public void addAuth(AuthData authData) throws ResponseException {
    try(var conn = getConnection()){
      var token = authData.authToken();
      var username = authData.username();
      var preparedStatement = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES(?,?);");
      preparedStatement.setString(1, token);
      preparedStatement.setString(2, username);
      preparedStatement.executeUpdate();
    }
    catch(Exception e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
  }

  @Override
  public AuthData getAuth(String token) throws ResponseException {
    try(var conn = getConnection()) {
      var prepped = conn.prepareStatement("SELECT * FROM auth WHERE authToken = ?;");
      prepped.setString(1, token);
      var result = prepped.executeQuery();
      if(result.next()){
        return new AuthData(result.getString("authToken"), result.getString("username"));
      }
    }
    catch(Exception e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }
    return null;
  }

  @Override
  public void deleteAuth(String token) throws ResponseException {
    try(var conn = getConnection()) {
      var prepped=conn.prepareStatement("DELETE FROM auth WHERE authToken = ?;");
      prepped.setString(1, token);
      prepped.executeUpdate();
    }
    catch(Exception e){
      throw new ResponseException(500, "Error connecting to database: " + e.getMessage());
    }

  }

  @Override
  public int createGame(String gameName) {
    return 0;
  }

  @Override
  public void deleteGame(int gameId) {

  }

  @Override
  public void updateGame(GameData game) {

  }

  @Override
  public GameData getGame(String gameName) {
    return null;
  }

  @Override
  public GameData getGame(int gameId) {
    return null;
  }

  @Override
  public ArrayList<GameData> getAllGames() {
    return null;
  }

  @Override
  public void clearAll() throws ResponseException {
    try(var conn = getConnection()){
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