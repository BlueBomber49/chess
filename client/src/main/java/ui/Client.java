package ui;

import exception.BadInputException;
import exception.ResponseException;

import java.util.Arrays;
import java.util.Objects;

public class Client {
  private ServerFacade facade;
  private String auth;
  private String currentUser;
  public State state;
  public Client(String url){
    facade = new ServerFacade(url);
    auth = null;
    currentUser = null;
    state = State.LOGGED_OUT;
  }

  public void run(){
    System.out.println("Welcome to TerminalChess!  Login to start.");
    System.out.println(this.help());
    while(state != State.QUIT){
      new Repl(this).run();
    }
    System.out.println("See ya later!");
  }

  public String eval(String input){
    var tokens = input.toLowerCase().split(" ");
    String cmd;
    if(tokens.length > 0){
      cmd = tokens[0];
    }
    else{
      cmd = "";
    }
    var params = Arrays.copyOfRange(tokens, 1, tokens.length);
    if(Objects.equals(cmd, "help")){
      return this.help();
    }
    else if(Objects.equals(cmd, "quit")){
      return this.quit();
    }
    else {
      switch (state) {
        case LOGGED_OUT -> {
          switch (cmd) {
            case "help" -> {
              return this.help();
            }
            case "quit" -> {
              this.quit();
              return "quit";
            }
            case "register" -> {
              return this.register(params);
            }
            case "login" -> {
              return this.login(params);
            }
            default -> {
              return "Command not recognized.  Type 'help' for a list of commands";
            }
          }
        }
        case LOGGED_IN -> {
          switch (cmd) {
            case "logout" -> {
              return this.logout();
            }
            case "create" -> {
              return "creates game";
            }
            case "list" -> {
              return "tbd";
            }
            case "join" -> {
              return "print board";
            }
            case "observe" -> {
              return "Phase 6";
            }
            default -> {
              return "Command not recognized.  Type 'help' for a list of commands";
            }
          }
        }
        case PLAYING_GAME -> {
          return "Can't do this yet lol";
        }
        default -> {return "Broke";}
      }
    }
  }


  public String help(){
    return switch(state){
      case LOGGED_OUT -> """
              help:  Gets a list of available commands
              register [username] [password] [email]: Register as a new user
              login [username] [password]: Login as an existing user
              quit:  Terminates the program
              """;
      case LOGGED_IN -> """
              help:  Gets a list of available commands
              logout:  Logs out and returns you to the previous page
              list:  Gets a list of available games with their id's
              create [game name]:  Creates a new game with the given name
              join [ID] [WHITE|BLACK]:  Joins the game as the chosen color
              observe [ID]:  Joins the game as an observer
              quit:  Terminates the program
              """;
      case PLAYING_GAME -> "Phase 6 stuff";
      default -> "Something broke";
    };
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
      facade.register(username, password, email);
      state = State.LOGGED_IN;
      return "Registration successful.  Welcome, " + username + "!";
    }
    catch (Exception e){
      if(e.getClass() == ResponseException.class){
        return "That username is taken.  Please select a different username";
      }
      return "Error: " + e.getMessage(); //Change this?
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
      currentUser = authData.username();
      state = State.LOGGED_IN;
      return "Login successful.  Welcome, " + username + "!";
    }
    catch(Exception e){
      return "Error: " + e.getMessage(); //Change this at some point
    }
  }

  public String logout(){
    try {
      facade.logout(auth);
      auth = null;
      currentUser = null;
      state = State.LOGGED_OUT;
      return "Logged out.  See ya later!";
    }
    catch(Exception e) {
      return "Error: " + e.getMessage(); //Change at some point
    }
  }
}
