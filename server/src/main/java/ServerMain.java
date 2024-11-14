import chess.*;
import dataaccess.SQLDataAccess;
import exception.ResponseException;
import server.Server;

public class ServerMain {
    public static void main(String[] args) throws ResponseException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        var server = new Server();
        server.run(8080);
    }
}