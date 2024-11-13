package ui;

import java.util.Scanner;

public class PostLoginRepl extends Repl {
  private Client client;
  public PostLoginRepl(Client client, State state){
    super(client, state);
  }


  public String eval_cmd(String cmd, String[] params) {
    return null;
  }


}
