package chess.MoveCalculators;

import chess.*;
import chess.MoveCalculators.MoveCalculator;

import java.util.ArrayList;

public class KingMoveCalculator extends MoveCalculator {
  /**
   * @param board
   * @param pieceType
   * @param color
   * @param position
   */
  public KingMoveCalculator(ChessBoard board, ChessPiece.PieceType pieceType, ChessGame.TeamColor color, ChessPosition position) {
    super(board, pieceType, color, position);
  }

  public ArrayList<ChessMove> CalculateValidMoves(){
    ChessPosition start = super.position;
    ChessPosition end = start;
    ChessGame.TeamColor color = super.color;
    ArrayList<ChessMove> moveList = new ArrayList<>();
    /**
     * Works from the bottom left corner, left to right, then up.
     * Checks to see if you're moving onto a friendly piece
     * Or if your starting and ending positions are equal (Not a move)
     */
    for(int row_diff = -1; row_diff < 2; row_diff++){
      for(int col_diff = -1; col_diff < 2; col_diff++){
        int endRow = start.getRow() + row_diff;
        int endCol = start.getColumn() + col_diff;
        if(endRow < 1 || endRow > 8 || endCol < 1 || endCol> 8) {
          continue;
        }
        end = new ChessPosition(start.getRow() + row_diff, start.getColumn() + col_diff);
        if(end == start){
          continue;
        }
        else if(board.getPiece(end) != null && board.getPiece(end).getTeamColor() == color){
          continue;
        }
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
      }
    }
    return moveList;
  }
}
