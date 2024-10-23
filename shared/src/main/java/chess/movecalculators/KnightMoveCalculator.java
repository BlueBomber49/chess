package chess.movecalculators;

import chess.*;

import java.util.ArrayList;

public class KnightMoveCalculator extends MoveCalculator{


  /**
   * @param board
   * @param pieceType
   * @param color
   * @param position
   */
  public KnightMoveCalculator(ChessBoard board, ChessPiece.PieceType pieceType, ChessGame.TeamColor color, ChessPosition position) {
    super(board, pieceType, color, position);
  }

  public ArrayList<ChessMove> calculateValidMoves(){

    ChessPosition start = super.position;
    ChessPosition end = start;
    ChessGame.TeamColor color = super.color;
    ArrayList<ChessMove> moveList = new ArrayList<>();

    Integer[] RowModifiers = new Integer[]{2, 2, -2, -2, 1, -1, 1, -1};
    Integer[] ColumnModifiers = new Integer[]{1, -1, 1, -1, 2, 2, -2, -2};


    for(int i = 0; i < RowModifiers.length; i++){
      int endRow = start.getRow() + RowModifiers[i];
      int endCol = start.getColumn() + ColumnModifiers[i];
      if(1 <= endRow && 8 >= endRow && 1 <= endCol && 8 >= endCol){
        end = new ChessPosition(endRow, endCol);
        if(board.getPiece(end) == null || board.getPiece(end).getTeamColor() != color){  //Empty end space
          ChessMove moveToAdd = new ChessMove(start,end,null);
          moveList.add(moveToAdd);
        }
      }
    }
    return moveList;
  }
}
