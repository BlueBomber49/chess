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
    ChessPosition start = super.position;
    ChessPosition end = start;
    ArrayList<ChessMove> moveList = new ArrayList<>();

    while(end.getRow() < 8 && end.getColumn() < 8){ //Increase row and column (Upward right diagonal)
      end = new ChessPosition(end.getRow()+1, end.getColumn()+1);
      if(board.getPiece(end) == null){
        ChessMove moveToAdd = new ChessMove(start,end,null);
        moveList.add(moveToAdd);
      }
    }
    return moveList;
  }
}
