import chess.*;
import ui.*;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        System.out.println("♕ 240 Chess Client: ");
        Client client = new Client("http://localhost:8080");

        /*
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a message you want to echo");
        while (true) {
            client.send(scanner.nextLine());
            //this.session.getBasicRemote().sendText(msg);
        }

         */

    }
}