import chess.*;
import dataaccess.SQLDataAccess;
import dataaccess.exception.ResponseException;
import model.UserData;
import server.Server;

import java.sql.SQLException;

import static java.lang.Integer.parseInt;

public class Main {
    public static void main(String[] args) throws ResponseException {
        var SQL = new SQLDataAccess();
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        var server = new Server();
        server.run(8080);
    }
}