package ui.evaluators;

import chess.*;
import exception.ResponseException;
import ui.Client;
import ui.State;
import websocket.WebsocketFacade;

import java.util.Locale;
import java.util.Map;

import static ui.EscapeSequences.*;

public class InGameEvaluator {

  ChessGame game;
  ChessGame.TeamColor color;
  Client client;
  WebsocketFacade ws;
  String auth;
  Integer currentGameID;
  State state;
  public InGameEvaluator(String auth, Integer currentGameID, State state, ChessGame game,
                         ChessGame.TeamColor color, Client client, WebsocketFacade ws){
    this.game = game;
    this.color = color;
    this.client = client;
    this.ws = ws;
    this.auth = auth;
    this.currentGameID = currentGameID;
    this.state = state;
  }

  public String eval(String cmd, String[] params) {
    switch(cmd) {
      case "leave" -> {
        return leaveGame();
      }
      case "redraw" -> {
        return client.drawBoard(game, color);
      }
      case "resign" -> {
        return resignGame();
      }
      case "highlight" -> {
        return "I would love to highlight the board right now";
      }
      case "move" -> {
        return makeMove(params);
      }
      default -> {
        return "Not a recognized command.  Type 'help' for a list of commands";
      }
    }
  }
  public String leaveGame(){
    try {
      state=State.LOGGED_IN;
      ws.leaveGame(auth, currentGameID);
      return "You have left the game";
    }
    catch (Exception e){
      return "Error: " + e.getMessage();
    }
  }

  public String resignGame(){
    try{
      ws.resignGame(auth, currentGameID);
    } catch (ResponseException e) {
      return "Error: " + e.getMessage();
    }
    return "You have resigned";
  }

  public String makeMove(String[] params){
    try {
      if (params.length < 2 || params.length > 3) {
        return "Please use format 'move [start column][start row] [end column][end row] [Promotion R|B|N|Q]";
      }
      var colMap=Map.of("a", 1, "b", 2, "c", 3, "d", 4,
              "e", 5, "f", 6, "g", 7, "h", 8);
      String start=params[0];
      String end=params[1];
      if (!start.matches("\\b[a-h][1-8]\\b") || !end.matches("\\b[a-h][1-8]\\b")) {
        return "Please use format 'move [start column][start row] [end column][end row]";
      }
      int startRow=Integer.parseInt(start.substring(1));
      int startCol=colMap.get(start.substring(0, 1));
      int endRow=Integer.parseInt(end.substring(1));
      int endCol=colMap.get(end.substring(0, 1));
      ChessPosition startPos=new ChessPosition(startRow, startCol);
      ChessPosition endPos=new ChessPosition(endRow, endCol);
      ChessMove move;
      if (params.length == 3) {
        String promotionParam=params[2];
        ChessPiece.PieceType promotionPiece;
        switch (promotionParam.toUpperCase()) {
          case "R" -> {
            promotionPiece=ChessPiece.PieceType.ROOK;
          }
          case "N" -> {
            promotionPiece=ChessPiece.PieceType.KNIGHT;
          }
          case "B" -> {
            promotionPiece=ChessPiece.PieceType.BISHOP;
          }
          case "Q" -> {
            promotionPiece=ChessPiece.PieceType.QUEEN;
          }
          default -> {
            return "Invalid Promotion Piece";
          }
        }
        move=new ChessMove(startPos, endPos, promotionPiece);
      } else {
        move=new ChessMove(startPos, endPos, null);
      }
      ws.makeMove(auth, currentGameID, move);
    }
    catch(Exception e){
      return "Error: " + e.getMessage();
    }
    return "";
  }

  public String getAuth() {
    return auth;
  }

  public Integer getCurrentGameID() {
    return currentGameID;
  }

  public State getState() {
    return state;
  }

  public ChessGame getGame() {
    return game;
  }

  public ChessGame.TeamColor getColor() {
    return color;
  }

  public WebsocketFacade getWs() {
    return ws;
  }
}
