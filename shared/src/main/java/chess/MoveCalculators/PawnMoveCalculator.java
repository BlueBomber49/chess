package chess.MoveCalculators;

import chess.*;

import java.util.ArrayList;

public class PawnMoveCalculator extends MoveCalculator{
  /**
   * @param board
   * @param pieceType
   * @param color
   * @param position
   */
  public PawnMoveCalculator(ChessBoard board, ChessPiece.PieceType pieceType, ChessGame.TeamColor color, ChessPosition position) {
    super(board, pieceType, color, position);
  }

  public ArrayList<ChessMove> CalculateValidMoves(){
    throw new RuntimeException("MoveCalculator Not Implemented");
    //White: moves by increasing row.
    //If row = 2, can move up 2
    //Can capture if enemy piece is diagonally forward (Col +1/-1)
    //If row = 8, valid moves = all promotion pieces

    //Black: moves by decreasing row
    //If row = 7, can move down 2
    //Can capture if enemy piece is diagonally down (Col +1/-1)
    //If row = 1, valid moves = all promotion pieces
  }
}
