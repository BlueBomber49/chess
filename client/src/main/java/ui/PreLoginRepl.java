package ui;

import java.util.Scanner;
import java.util.Arrays;

public class PreLoginRepl extends Repl{
private Client client;
private State state;
  public PreLoginRepl(Client client, State state) {
    super(client, state);
    this.client = client;
    this.state = state;
  }

  public String eval_cmd(String cmd, String[] params){
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
        var result =  client.register(params);
        if(result.startsWith("Registration successful.")){
          state = State.LOGGED_IN;
        }
        return result;
      }
      case "login" -> {
        var result = client.login(params);
        if(result.startsWith("Login successful.")){
          state = State.LOGGED_IN;
        }
        return result;
      }
      default -> {
        return "Command not recognized.  Type 'help' for a list of commands";
      }
    }

  }

}
