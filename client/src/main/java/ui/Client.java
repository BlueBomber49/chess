package ui;

public class Client {
  private ServerFacade facade;
  private String auth;
  private String currentUser;
  public Client(String url){
    facade = new ServerFacade(url);
    auth = null;
    currentUser = null;
  }


  public String help(State state){
    switch(state){
      case NOT_LOGGED_IN -> {return "";}
      case LOGGED_IN -> {return "Not yet implemented";}
      case PLAYING_GAME -> {return "Phase 6 stuff";}
      default -> {return "Something broke";}
    }
  }
  public String register(String username, String password, String email){
    try{
      facade.register(username, password, email);
      return "Registration successful.  Welcome, " + username + "!";
    }
    catch (Exception e){
      return "Error: " + e.getMessage(); //Change this
    }
  }

  public String login(String username, String password){
    try {
      var authData = facade.login(username, password);
      auth = authData.authToken();
      currentUser = authData.username();
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
      return "Logged out.  See ya later!";
    }
    catch(Exception e) {
      return "Error: " + e.getMessage(); //Change at some point
    }
  }
}
