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
    ChessPosition start = super.position;
    ChessPosition end = start;
    ChessGame.TeamColor color = super.color;
    ArrayList<ChessMove> moveList = new ArrayList<>();

    if(color == ChessGame.TeamColor.WHITE){ //White pawn moves
      //Edge cases
      if(start.getRow() == 2){ //Double move option

      } else if(start.getRow() == 5){ //En Passant

      }


      if(start.getColumn() != 1){ //Right capture possible

      }
      if(start.getColumn() != 8){ //Left capture possible

      }

      //Create all moves (move forward if nobody's in the way, capture if there are pieces to capture)

      if(start.getRow() == 7){ //Promotion pieces need added to all moves  Will affect all moves

      }


    } else{ //Black pawn moves

    }
    //White: moves by increasing row.
    //If row = 2, can move up 2
    //Can capture if enemy piece is diagonally forward (Col +1/-1)
    //If row = 7, valid moves = all promotion pieces

    //Black: moves by decreasing row
    //If row = 7, can move down 2
    //Can capture if enemy piece is diagonally down (Col +1/-1)
    //If row = 2, valid moves = all promotion pieces

    return moveList;
  }
}
