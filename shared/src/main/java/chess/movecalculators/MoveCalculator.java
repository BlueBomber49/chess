package chess.movecalculators;

import chess.*;

import java.util.ArrayList;

public class MoveCalculator {
  protected ChessBoard board;
  protected final ChessPiece.PieceType pieceType;
  protected final ChessGame.TeamColor color;
  protected ChessPosition position;
  protected ArrayList<ChessMove> validMoves;
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
    if(pieceType == ChessPiece.PieceType.KING){
      KingMoveCalculator calc = new KingMoveCalculator(board, pieceType, color, position);
      validMoves = calc.calculateValidMoves();
    }
    else if(pieceType == ChessPiece.PieceType.QUEEN){
      QueenMoveCalculator calc = new QueenMoveCalculator(board, pieceType, color, position);
      validMoves = calc.calculateValidMoves();
    }
    else if(pieceType == ChessPiece.PieceType.BISHOP){
      BishopMoveCalculator calc = new BishopMoveCalculator(board, pieceType, color, position);
      validMoves = calc.calculateValidMoves();
    }
    else if(pieceType == ChessPiece.PieceType.KNIGHT){
      KnightMoveCalculator calc = new KnightMoveCalculator(board, pieceType, color, position);
      validMoves = calc.calculateValidMoves();
    }
    else if(pieceType == ChessPiece.PieceType.ROOK){
      RookMoveCalculator calc = new RookMoveCalculator(board, pieceType, color, position);
      validMoves = calc.calculateValidMoves();
    }
    else if(pieceType == ChessPiece.PieceType.PAWN){
      PawnMoveCalculator calc = new PawnMoveCalculator(board, pieceType, color, position);
      validMoves = calc.calculateValidMoves();
    }
    else{
      throw new RuntimeException("Piece Type undefined");
    }

    return validMoves;
  }



}
