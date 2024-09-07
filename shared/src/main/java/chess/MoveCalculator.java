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

  public ArrayList<ChessMove> GetMoves(){
    if(pieceType == ChessPiece.PieceType.BISHOP){
      BishopMoveCalculator calc = new BishopMoveCalculator(board, pieceType, color, position);
      validMoves = calc.CalculateValidMoves();
    }
    return validMoves;
  }



}
