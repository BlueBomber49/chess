package chess.movecalculators;

import chess.*;

import java.util.ArrayList;

public class QueenMoveCalculator extends MoveCalculator{
  /**
   * @param board
   * @param pieceType
   * @param color
   * @param position
   */
  public QueenMoveCalculator(ChessBoard board, ChessPiece.PieceType pieceType, ChessGame.TeamColor color, ChessPosition position) {
    super(board, pieceType, color, position);
  }

  public ArrayList<ChessMove> calculateValidMoves(){
    ChessPosition start = super.position;
    ChessPosition end = start;
    ChessGame.TeamColor color = super.color;
    ArrayList<ChessMove> moveList = new ArrayList<>();

    //RookMovement

    //Upward movement
    while(end.getRow() < 8){
      end = new ChessPosition(end.getRow() + 1,end.getColumn());
      if(board.getPiece(end) == null){ //No piece in target
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
      }
      else if(board.getPiece(end).getTeamColor() != color){ //Enemy piece in target
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
        break;
      }
      else{ //Friendly piece in target
        break;
      }
    }

    end = start; //Reset end position

    //Downward movement
    while(end.getRow() > 1){
      end = new ChessPosition(end.getRow() - 1,end.getColumn());
      if(board.getPiece(end) == null){ //No piece in target
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
      }
      else if(board.getPiece(end).getTeamColor() != color){ //Enemy piece in target
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
        break;
      }
      else{ //Friendly piece in target
        break;
      }
    }

    end = start; //Reset end position

    //Left movement
    while(end.getColumn() > 1) {
      end = new ChessPosition(end.getRow() ,end.getColumn() - 1);
      if(board.getPiece(end) == null){ //No piece in target
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
      }
      else if(board.getPiece(end).getTeamColor() != color){ //Enemy piece in target
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
        break;
      }
      else{ //Friendly piece in target
        break;
      }
    }

    end = start; //Reset end position

    //Right movement
    while(end.getColumn() < 8){
      end = new ChessPosition(end.getRow(),end.getColumn() + 1);
      if(board.getPiece(end) == null){ //No piece in target
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
      }
      else if(board.getPiece(end).getTeamColor() != color){ //Enemy piece in target
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
        break;
      }
      else{ //Friendly piece in target
        break;
      }
    }


    end = start; //Reset end position
    //BishopMoves

    //Increase row and increase column (Upward right diagonal)
    while(end.getRow() < 8 && end.getColumn() < 8){
      end = new ChessPosition(end.getRow()+1, end.getColumn()+1);
      if(board.getPiece(end) == null){  //Empty end space
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
      }
      else if(board.getPiece(end).getTeamColor() != color){ //Opponent's piece on target
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
        break;
      }
      else{ //Friendly piece on target
        break;
      }
    }

    end = start; //reset end position

    //Increase row and decrease column (Upward left diagonal)
    while(end.getRow() < 8 && end.getColumn() > 1){
      end = new ChessPosition(end.getRow()+1, end.getColumn()-1);
      if(board.getPiece(end) == null){  //Empty end space
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
      }
      else if(board.getPiece(end).getTeamColor() != color){ //Opponent's piece on target
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
        break;
      }
      else{ //Friendly piece on target
        break;
      }
    }

    end = start; //reset end position

    //Decrease row and increase column (Downward right diagonal)
    while(end.getRow() > 1 && end.getColumn() < 8){
      end = new ChessPosition(end.getRow()-1, end.getColumn()+1);
      if(board.getPiece(end) == null){  //Empty end space
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
      }
      else if(board.getPiece(end).getTeamColor() != color){ //Opponent's piece on target
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
        break;
      }
      else{ //Friendly piece on target
        break;
      }
    }

    end = start; //reset end position

    //Decrease row and decrease column (Downward left diagonal)
    while(end.getRow() > 1 && end.getColumn() > 1){
      end = new ChessPosition(end.getRow()-1, end.getColumn()-1);
      if(board.getPiece(end) == null){  //Empty end space
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
      }
      else if(board.getPiece(end).getTeamColor() != color){ //Opponent's piece on target
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
        break;
      }
      else{ //Friendly piece on target
        break;
      }
    }

    return moveList;
  }
}
