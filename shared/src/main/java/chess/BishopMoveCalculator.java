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
    return new ArrayList<>();
  }
}
