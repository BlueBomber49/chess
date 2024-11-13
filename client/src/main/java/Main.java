import chess.*;
import server.Server;
import ui.*;

public class Main {
    public static void main(String[] args) throws Exception {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var server = new Server();
        var port = server.run(0);
        var client = new Client("http://localhost:" + port);
        client.run();
        System.exit(0);
    }
}