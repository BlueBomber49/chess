import chess.*;
import ui.*;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        System.out.println("♕ 240 Chess Client: ");
        Client client = new Client("http://localhost:8080");

        client.run();

    }
}