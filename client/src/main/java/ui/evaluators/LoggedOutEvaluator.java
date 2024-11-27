package ui.evaluators;

import exception.ResponseException;
import model.AuthData;
import responseclasses.GameResponse;
import ui.ServerFacade;
import ui.State;

import java.util.ArrayList;

public class LoggedOutEvaluator {

  private ServerFacade facade;
  private ArrayList<GameResponse> games;
  private String auth;
  private State state;

  public LoggedOutEvaluator(ServerFacade facade, ArrayList<GameResponse> games, State state){
    this.facade = facade;
    this.games = games;
    this.state = state;
  }

  public String eval(String cmd, String[] params){
    switch (cmd) {
      case "quit" -> {
        this.quit();
        return "quit";
      }
      case "register" -> {
        var response=this.register(params);
        try {
          games=facade.listGames(auth);
        }
        catch(Exception ignored){
        }
        return response;
      }
      case "login" -> {
        var response=this.login(params);
        try {
          games=facade.listGames(auth);
        }
        catch(Exception ignored){
        }
        return response;
      }
      default -> {
        return "Command not recognized.  Type 'help' for a list of commands";
      }
    }
  }

  public String quit(){
    state = State.QUIT;
    return "quit";
  }

  public String register(String[] params){
    try{
      if(params.length != 3){
        return "Incorrect register format.  Please use the format 'register [username] [password] [email]'";
      }
      var username = params[0];
      var password = params[1];
      var email = params[2];
      AuthData authData = facade.register(username, password, email);
      auth = authData.authToken();
      state = State.LOGGED_IN;
      return "Registration successful.  Welcome, " + username + "!";
    }
    catch (Exception e){
      if(e.getClass() == ResponseException.class){
        return "That username is taken.  Please select a different username";
      }
      return "Error: " + e.getMessage();
    }
  }

  public String login(String[] params ){
    try {
      if(params.length != 2){
        return "Incorrect login format.  Please use the format 'login [username] [password] ";
      }
      var username = params[0];
      var password = params[1];
      var authData = facade.login(username, password);
      auth = authData.authToken();
      state = State.LOGGED_IN;
      return "Login successful.  Welcome, " + username + "!";
    }
    catch(Exception e){
      if(e.getMessage().startsWith("failure: 401")){
        return "Username or password incorrect";
      }
      return "Error: " + e.getMessage(); //Change this at some point
    }
  }

  public State getState(){
    return state;
  }

  public String getAuth(){
    return auth;
  }

  public ArrayList<GameResponse> getGames(){
    return games;
  }
}
