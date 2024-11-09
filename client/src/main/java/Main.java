import chess.*;
import dataaccess.SQLDataAccess;
import exception.BadInputException;
import exception.ResponseException;
import exception.UsernameTakenException;
import model.UserData;
import server.Server;
import service.UserService;
import ui.ServerFacade;

public class Main {
    public static void main(String[] args) throws Exception {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var server = new Server();

    }
}