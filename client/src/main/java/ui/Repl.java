package ui;

import java.util.Scanner;

public class Repl {
  private Client client;
  private Scanner scanner;
  public Repl(Client client){
    this.client = client;
    this.scanner = new Scanner(System.in);
  }
  public void run() {
  String result="";
    while(result != "quit") {
      System.out.print("[" + client.state + "]>> ");
      var input=scanner.nextLine();
      try {
        result = client.eval(input);
        System.out.printf(result);
        System.out.println();
      }
      catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
      }
  }
}
}