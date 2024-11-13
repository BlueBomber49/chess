package ui;

import java.util.Arrays;
import java.util.Scanner;

public abstract class Repl {
  private Scanner scanner;
  private Client client;
  private State state;
  public Repl(Client client, State state){
    this.client = client;
    scanner = new Scanner(System.in);
    this.state = state;
  }

  public void run(State expectedState){
    String result = "";
    while(state == expectedState){
      System.out.print(">> ");
      var input = scanner.nextLine();

      try{
        result = eval_input(input);
        System.out.println(result);
      }
      catch(Exception e){
        System.out.println("Error: " + e.getMessage());
      }
    }
  }

  public String eval_input(String input){
    var tokens = input.toLowerCase().split(" ");
    String cmd;
    if(tokens.length > 0){
      cmd = tokens[0];
    }
    else{
      cmd = "";
    }
    var params = Arrays.copyOfRange(tokens, 1, tokens.length);
    return eval_cmd(cmd, params);
  }

  public abstract String eval_cmd(String cmd, String[] params);
}
