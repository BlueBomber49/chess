package chess.MoveCalculators;

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

  public ArrayList<ChessMove> CalculateValidMoves(){
    throw new RuntimeException("MoveCalculator Not Implemented");
  }
}
