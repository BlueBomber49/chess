import chess.*;
import dataaccess.SQLDataAccess;
import model.UserData;
import server.Server;

import java.sql.SQLException;

import static java.lang.Integer.parseInt;

public class Main {
    public static void main(String[] args) throws SQLException {
        var SQL = new SQLDataAccess();
        SQL.addUser(new UserData("Felix", "fix", "so@crosswithyou"));
        System.out.println(SQL.getUser("Felix"));
        System.out.println(SQL.getUser("Bob"));
        System.out.println(SQL.getUser("Hulk"));
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        var server = new Server();
        server.run(8080);
    }
}