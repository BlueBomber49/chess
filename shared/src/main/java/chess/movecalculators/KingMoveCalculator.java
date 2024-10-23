package chess.movecalculators;

import chess.*;

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

  public ArrayList<ChessMove> calculateValidMoves(){
    ChessPosition start = super.position;
    ChessPosition end = start;
    ChessGame.TeamColor color = super.color;
    ArrayList<ChessMove> moveList = new ArrayList<>();
    /**
     * Works from the bottom left corner, left to right, then up.
     * Checks to see if you're moving onto a friendly piece
     * Or if your starting and ending positions are equal (Not a move)
     */
    for(int rowDiff = -1; rowDiff < 2; rowDiff++){
      for(int colDiff = -1; colDiff < 2; colDiff++){
        int endRow = start.getRow() + rowDiff;
        int endCol = start.getColumn() + colDiff;
        if(endRow < 1 || endRow > 8 || endCol < 1 || endCol> 8) {
          continue;
        }
        end = new ChessPosition(start.getRow() + rowDiff, start.getColumn() + colDiff);
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
