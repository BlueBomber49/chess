import chess.*;
import ui.*;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        System.out.println("♕ 240 Chess Client: ");
        Client client = new Client("http://localhost:8080");
        client.drawBoard(new ChessGame(), ChessGame.TeamColor.BLACK);
        System.out.println();
        client.drawBoard(new ChessGame(), ChessGame.TeamColor.WHITE);
        client.run();

    }
}