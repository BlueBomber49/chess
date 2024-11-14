import chess.*;
import ui.*;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        Client client = new Client("http://localhost:8080");
        client.drawBoard(new ChessGame(), ChessGame.TeamColor.WHITE);
    }
}