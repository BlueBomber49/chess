package chess;

import java.util.ArrayList;

public class BishopMoveCalculator extends MoveCalculator {


  /**
   * @param board
   * @param pieceType
   * @param color
   * @param position
   */
  public BishopMoveCalculator(ChessBoard board, ChessPiece.PieceType pieceType, ChessGame.TeamColor color, ChessPosition position) {
    super(board, pieceType, color, position);
  }

  public ArrayList<ChessMove> CalculateValidMoves(){
    ChessPosition start = super.position;
    ChessPosition end = start;
    ChessGame.TeamColor color = super.color;
    ArrayList<ChessMove> moveList = new ArrayList<>();

    while(end.getRow() < 8 && end.getColumn() < 8){ //Increase row and increase column (Upward right diagonal)
      end = new ChessPosition(end.getRow()+1, end.getColumn()+1);
      if(board.getPiece(end) == null){  //Empty end space
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
      }
      else if(board.getPiece(end).getTeamColor() != color){ //Opponent's piece on end space
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
        break;
      }
      else{ //Friendly piece on end space
        break;
      }
    }

    end = start; //reset end position

    while(end.getRow() < 8 && end.getColumn() > 1){ //Increase row and decrease column (Upward left diagonal)
      end = new ChessPosition(end.getRow()+1, end.getColumn()-1);
      if(board.getPiece(end) == null){  //Empty end space
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
      }
      else if(board.getPiece(end).getTeamColor() != color){ //Opponent's piece on end space
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
        break;
      }
      else{ //Friendly piece on end space
        break;
      }
    }

    end = start; //reset end position

    while(end.getRow() > 1 && end.getColumn() < 8){ //Decrease row and increase column (Downward right diagonal)
      end = new ChessPosition(end.getRow()-1, end.getColumn()+1);
      if(board.getPiece(end) == null){  //Empty end space
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
      }
      else if(board.getPiece(end).getTeamColor() != color){ //Opponent's piece on end space
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
        break;
      }
      else{ //Friendly piece on end space
        break;
      }
    }

    end = start; //reset end position

    while(end.getRow() > 1 && end.getColumn() > 1){ //Decrease row and decrease column (Downward left diagonal)
      end = new ChessPosition(end.getRow()-1, end.getColumn()-1);
      if(board.getPiece(end) == null){  //Empty end space
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
      }
      else if(board.getPiece(end).getTeamColor() != color){ //Opponent's piece on end space
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
        break;
      }
      else{ //Friendly piece on end space
        break;
      }
    }
    return moveList;
  }
}
