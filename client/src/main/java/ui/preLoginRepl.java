package ui;

import java.util.Scanner;
import java.util.Arrays;

public class preLoginRepl {
  private Scanner scanner;
  private Client client;
  private State state;
  public preLoginRepl(Client client, State state){
    this.client = client;
    scanner = new Scanner(System.in);
    this.state = state;
  }

  public void run(){
    String result = "";
    while(state == State.LOGGED_OUT){
      System.out.println(">> ");
      var input = scanner.nextLine();

      try{
        result = eval(input);
        System.out.println(result);
      }
      catch(Exception e){
        System.out.println("Error: " + e.getMessage());
      }
    }
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

    switch(cmd){
      case "help" -> {
        return client.help();
      }
      case "quit" -> {
        client.quit();
        state = State.QUIT;
        return "quit";
      }
      case "register" -> {
        return client.register(params);
      }
      case "login" -> {
        return client.login(params);
      }
      default -> {
        return "Command not recognized.  Type 'help' for a list of commands";
      }
    }

  }

}
