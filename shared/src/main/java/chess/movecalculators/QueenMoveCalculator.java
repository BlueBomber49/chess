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

    var rookMoves = new RookMoveCalculator(board, pieceType, color, position).calculateValidMoves();

    //BishopMoves

    var bishopMoves = new BishopMoveCalculator(board, pieceType, color, position).calculateValidMoves();

    moveList.addAll(rookMoves);
    moveList.addAll(bishopMoves);
    return moveList;
  }
}
