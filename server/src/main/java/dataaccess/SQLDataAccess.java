package dataaccess;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;
import java.util.ArrayList;

public class SQLDataAccess implements DataAccess{
  private Connection conn;
  public SQLDataAccess() throws SQLException {
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
      System.out.println("Successful initialization");
    }
    catch (SQLException e){
      System.out.println("Failed to connect: " + e);
    }
  }
  Connection getConnection() throws SQLException {
    return DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "SQLPassword");
  }

  void configureDatabase() throws SQLException {

  }

  @Override
  public void addUser(UserData person) {

  }

  @Override
  public UserData getUser(String username) {
    return null;
  }

  @Override
  public void deleteUser(String username) {

  }

  @Override
  public void addAuth(AuthData authData) {

  }

  @Override
  public AuthData getAuth(String token) {
    return null;
  }

  @Override
  public void deleteAuth(String token) {

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
  public void clearAll() {

  }
}