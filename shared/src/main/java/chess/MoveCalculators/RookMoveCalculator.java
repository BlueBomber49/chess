package chess.MoveCalculators;

import chess.*;

import java.util.ArrayList;

public class RookMoveCalculator extends MoveCalculator{
  /**
   * @param board
   * @param pieceType
   * @param color
   * @param position
   */
  public RookMoveCalculator(ChessBoard board, ChessPiece.PieceType pieceType, ChessGame.TeamColor color, ChessPosition position) {
    super(board, pieceType, color, position);
  }

  public ArrayList<ChessMove> CalculateValidMoves(){
    ChessPosition start = super.position;
    ChessPosition end = start;
    ChessGame.TeamColor color = super.color;
    ArrayList<ChessMove> moveList = new ArrayList<>();
    while(end.getRow() < 8){ //Upward movement
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

    while(end.getRow() > 1){ //Downward movement
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

    while(end.getColumn() > 1) { //Left movement
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

    while(end.getColumn() < 8){ //Right movement
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
    return moveList;
  }
}
