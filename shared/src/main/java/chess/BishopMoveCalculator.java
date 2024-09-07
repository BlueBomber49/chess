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
    ChessPosition start = new ChessPosition(1,1);
    ChessPosition end = new ChessPosition(2,2);
    ChessMove testMove = new ChessMove(start,end,null);
    ArrayList<ChessMove> moveList = new ArrayList<>();
    moveList.add(testMove);
    return moveList;
  }
}
