package chess;

import java.util.ArrayList;

public class MoveCalculator {
  private ChessBoard board;
  private final ChessPiece.PieceType pieceType;
  private final ChessGame.TeamColor color;
  private ChessPosition position;
  private ArrayList<ChessMove> validMoves;
  /**


   **/
  public MoveCalculator(ChessBoard board, ChessPiece.PieceType pieceType,
                        ChessGame.TeamColor color, ChessPosition position){
      this.board = board;
      this.pieceType = pieceType;
      this.color = color;
      this.position = position;
  }



}
