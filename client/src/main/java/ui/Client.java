package ui;

import exception.BadInputException;
import exception.ResponseException;

public class Client {
  private ServerFacade facade;
  private String auth;
  private String currentUser;
  private State state;
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
      switch(state){
        case LOGGED_OUT -> {
          new PreLoginRepl(this, state).run(State.LOGGED_OUT);
        }
        case LOGGED_IN ->{
          new PostLoginRepl(this, state).run(State.LOGGED_IN);
        }
        case PLAYING_GAME -> {
          System.out.println("Ya can't play yet bro");
          state = State.LOGGED_IN;
        }
      }
    }
    System.out.println("See ya later!");
  }

  public void quit(){
    state = State.QUIT;
  }


  public String help(){
    return switch(state){
      case LOGGED_OUT -> """
              help:  Gets a list of available commands
              register [username] [password] [email]: Register as a new user
              login [username] [password]: Login as an existing user
              quit:  Terminates the program
              """;
      case LOGGED_IN -> "Not yet implemented";
      case PLAYING_GAME -> "Phase 6 stuff";
      default -> "Something broke";
    };
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
